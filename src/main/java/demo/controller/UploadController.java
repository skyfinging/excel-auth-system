package demo.controller;

import demo.config.aop.AuthenticRequire;
import demo.dao.LastFileInfoRepository;
import demo.dao.bean.LastFileInfo;
import demo.service.excel.upload.IUploadHandle;
import demo.service.excel.upload.UploadSheetHandleFactory;
import demo.service.FileService;
import demo.service.IdGenServices;
import demo.service.IdGenEnum;
import demo.util.AESUtil;
import demo.util.ExcelUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * 上传excel文件
 * 需要先判断当前excel版本是不是最新的
 * 如果不是最新的，需要重新下载最新的excel进行编辑才可以上传
 * 如果是最新的，则将上传的excel文件与服务器上的excel文件内容合并
 * 为什么不能覆盖只能合并，因为用户可能没有所有列的权限，所以用户上传的excel列信息不完整
 */
@Controller
@Log4j2
public class UploadController {

    @Autowired
    private FileService fileService;

    @Autowired
    private IdGenServices idGenServices;

    @Autowired
    private LastFileInfoRepository lastFileInfoRepository;

    @Autowired
    private UploadSheetHandleFactory uploadSheetHandleFactory;

    @ResponseBody
    @RequestMapping("/upload")
    @Transactional(rollbackFor=Exception.class)
    @AuthenticRequire(value = "ROLE_UPLOAD")
    public Map<String, Object> upload(MultipartFile file, HttpServletRequest request) throws Exception {
        Map<String, Object> result = new HashMap<>();

        if(file==null){
            result.put("error", "文件为空，上传失败");
            return result;
        }
        File dir = fileService.getLastestDir(IdGenEnum.File);
        if (dir.exists() == false) {
            result.put("error", "文件还未初始化，请通知管理员上传文件");
            return result;
        }

        fileService.preparedDir(IdGenEnum.File);
        //准备工作
        String oldFileName = file.getOriginalFilename();
        long size = file.getSize();
        String userName = request.getUserPrincipal().getName();

        XSSFWorkbook uploadExcel = null;
        XSSFWorkbook lastestExcel = null;
        XSSFWorkbook newExcel = null;
        try {
            IdGenEnum.File.getReadWriteLock().writeLock().lock();

            long id = idGenServices.getCurrentId(IdGenEnum.File);
            //第1步，将上传的文件写入备份文件
            File tempFile = fileService.backupFile(IdGenEnum.File, userName + "_" + id, file.getBytes());
            //第2步，读取临时文件为excel格式
            try {
                uploadExcel = ExcelUtil.readExcel2007(tempFile);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                result.put("error", "上传的文件格式解析失败");
                return result;
            }
            //第3步，解析文件中的版本号,与当前的版本号进行比较
            long uploadVersion = getVersion(uploadExcel);
            if (uploadVersion != id) {
                result.put("error", "请从服务器上下载最新文件进行修改");
                return result;
            }
            //第5步，备份服务器上的文件
            fileService.backupLastestFile(IdGenEnum.File, id);
            //第6步，读取最新版本文件为excel格式

            try {
                lastestExcel = ExcelUtil.readExcel2007(fileService.getLastestFile(IdGenEnum.File, id + ""));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                result.put("error", "无法找到最新版本的文件");
                return result;
            }
            //第7步，将上传的excel合并到服务器上的excel
            try {
                newExcel = uploadExcel((AbstractAuthenticationToken) request.getUserPrincipal(), lastestExcel, uploadExcel);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                result.put("error", e.getMessage());
                return result;
            }
            //第8步，更新当前版本+1
            long nextId = idGenServices.getNextId(IdGenEnum.File);
            if(nextId==id){
                result.put("error", "无法更新版本号");
                throw new Exception("无法更新版本号");
            }
            //第9步，清除最新文件夹
            try {
                fileService.clearLastestFile(IdGenEnum.File);
            }catch (IOException e){
                //删除失败可不管
            }
            //第10步，将最新的excel保存到文件中
            ExcelUtil.writeToFile(newExcel, fileService.getLastestFile(IdGenEnum.File, "" + nextId));
            fileService.getLastestFile(IdGenEnum.File, id + "").delete();
            //第11步，记录数据库
            LastFileInfo lastFileInfo = new LastFileInfo(oldFileName, userName, size, new Timestamp(System.currentTimeMillis()), nextId);
            lastFileInfoRepository.save(lastFileInfo);
            result.put("success", "上传成功");
        }finally {
            IdGenEnum.File.getReadWriteLock().writeLock().unlock();
            if(uploadExcel!=null)
                uploadExcel.close();
            if(lastestExcel!=null)
                lastestExcel.close();
            if(newExcel!=null)
                newExcel.close();
        }
        return result;
    }

    private long getVersion(XSSFWorkbook uploadExcel){
        XSSFSheet sheet = uploadExcel.getSheet("Version");
        if(sheet==null)
            return -1;
        XSSFRow row = sheet.getRow(0);
        if(row==null)
            return -1;
        XSSFCell cell = row.getCell(0);
        if(cell==null)
            return -1;
        String value = cell.getStringCellValue();
        String version = AESUtil.decode(value);
        return Long.parseLong(version);
    }

    private XSSFWorkbook uploadExcel(AbstractAuthenticationToken principal, XSSFWorkbook lastestExcel, XSSFWorkbook uploadExcel) throws Exception {
        if(lastestExcel==null)
            return uploadExcel;
        if(uploadExcel==null)
            return lastestExcel;
        XSSFWorkbook newExcel = new XSSFWorkbook();
        //上传excel，处理原来就已经存在的sheet
        lastestExcel.sheetIterator().forEachRemaining(
                sheet -> uploadExistSheet(sheet, uploadExcel, newExcel, principal)
        );
        //上传excel，处理原来不存在的sheet
        uploadExcel.sheetIterator().forEachRemaining(
                sheet -> uploadNoExistSheet(sheet, lastestExcel, newExcel, principal)
        );
        ExcelUtil.clearStyleCache();
        lastestExcel.close();
        return newExcel;
    }

    /**
     * 处理原来就已经存在的sheet
     * 上传的excel中可能存在，也可能删掉了
     * @param lastestSheet
     * @param uploadExcel
     * @param newExcel
     * @param principal
     */
    private void uploadExistSheet(Sheet lastestSheet, XSSFWorkbook uploadExcel, XSSFWorkbook newExcel, AbstractAuthenticationToken principal){
        String sheetName = lastestSheet.getSheetName();
        Sheet uploadSheet = uploadExcel.getSheet(sheetName);
        IUploadHandle sheetMerge;
        if(uploadSheet==null) {     //uploadSheet不存在，说明上传的excel里面删掉了
            sheetMerge = uploadSheetHandleFactory.getRemoveSheetMergeHandler();
        }else
            sheetMerge = uploadSheetHandleFactory.getSheetMergeHandler(sheetName);
        if(sheetMerge!=null) {
            try {
                sheetMerge.merge(principal, newExcel, lastestSheet, uploadSheet);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    /**
     * 处理原来的excel中不存在的sheet
     * 上传的excel中新增的
     * @param uploadSheet
     * @param lastestExcel
     * @param newExcel
     * @param principal
     */
    private void uploadNoExistSheet(Sheet uploadSheet, XSSFWorkbook lastestExcel, XSSFWorkbook newExcel, AbstractAuthenticationToken principal){
        String sheetName = uploadSheet.getSheetName();
        XSSFSheet lastestSheet = lastestExcel.getSheet(sheetName);
        IUploadHandle sheetMerge = uploadSheetHandleFactory.getAddSheetMergeHandler();
        if(lastestSheet==null && sheetMerge!=null) {
            //lastSheet不存在，说明是上传的excel新增的
            try {
                sheetMerge.merge(principal, newExcel, null, uploadSheet);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
