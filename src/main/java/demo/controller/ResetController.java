package demo.controller;

import demo.config.aop.AuthenticRequire;
import demo.dao.LastFileInfoRepository;
import demo.dao.bean.LastFileInfo;
import demo.service.FileService;
import demo.service.IdGenServices;
import demo.service.IdGenEnum;
import demo.util.ExcelUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Iterator;
import java.util.Map;

@Log4j2
@Controller
public class ResetController {

    @Autowired
    private FileService fileService;

    @Autowired
    private LastFileInfoRepository lastFileInfoRepository;

    @Autowired
    private IdGenServices idGenServices;

    @RequestMapping("/reset")
    @ResponseBody
    @Transactional(rollbackFor=Exception.class)
    @AuthenticRequire(value = "ROLE_ADMIN")
    public Map<String, Object> reset(MultipartFile file, HttpServletRequest request) throws Exception {
        Map<String, Object> result = new HashMap<>();

        if(file==null){
            result.put("error", "文件为空，上传失败");
            return result;
        }
        //准备文件目录
        fileService.preparedDir(IdGenEnum.File);

        try {
            IdGenEnum.File.getReadWriteLock().writeLock().lock();
            //准备工作
            long id = idGenServices.getCurrentId(IdGenEnum.File);
            String oldFileName = file.getOriginalFilename();
            long size = file.getSize();
            String userName = request.getUserPrincipal().getName();
            String backupFileName = userName+"_reset_"+id;

            Long lastRequestId = (Long) request.getSession().getAttribute(IdGenEnum.File + "_version");
            if (lastRequestId == null || lastRequestId.equals(id) == false) {
                result.put("error", "有人更新了文件，请检查最新文件");
                return result;
            }

            //第1步，把上传的文件放到备份目录中
            fileService.backupFile(IdGenEnum.File, backupFileName, file.getBytes());
            fileService.backupLastestFile(IdGenEnum.File, id);
            //第2步，校验文件格式
            String validResult = validExcel(fileService.getBackupFile(IdGenEnum.File, backupFileName));
            if (validResult != null && validResult.isEmpty() == false) {
                result.put("error", validResult);
                return result;
            }
            //第4步，更新版本号
            long nextId = idGenServices.getNextId(IdGenEnum.File);
            if(nextId==id){
                result.put("error", "无法更新版本号");
                throw new Exception("无法更新版本号");
            }
            log.info("update file version: " + nextId);
            //第5步，把新文件放到最新文件夹中
            ExcelUtil.copyExcel(fileService.getBackupFile(IdGenEnum.File, backupFileName),
                    fileService.getLastestFile(IdGenEnum.File, nextId + ""));
            //第6步，删除旧版本文件
            fileService.getLastestFile(IdGenEnum.File, id+"").delete();
            //第6步，记录数据库
            LastFileInfo lastFileInfo = new LastFileInfo(oldFileName, userName, size, new Timestamp(System.currentTimeMillis()), nextId);
            lastFileInfoRepository.save(lastFileInfo);
        }finally {
            IdGenEnum.File.getReadWriteLock().writeLock().unlock();
        }
        return result;
    }

    private String validExcel(File file){
        XSSFWorkbook xwb;
        try {
            xwb = ExcelUtil.readExcel2007(file);
        } catch (Exception e) {
            log.warn(e.getMessage());
            return "无法读取excel文件";
        }
        XSSFSheet sheet = xwb.getSheet("座便器、小便器、妇洗器、柱盆");
        String result;
        if(sheet==null){
            result = "找不到sheet:座便器、小便器、妇洗器、柱盆";
        }else
            result = valideSheet(sheet);
        try {
            xwb.close();
        } catch (IOException e) {

        }
        return result;
    }

    private String valideSheet(Sheet sheet){
        Row head2 = sheet.getRow(1);
        if(head2==null)
            return "sheet《座便器、小便器、妇洗器、柱盆》找不到表头";
        Iterator<Cell> iterator = head2.iterator();
        while(iterator.hasNext()){
           Cell cell = iterator.next();
            Object value = ExcelUtil.getMergedRegionValue(cell);
            if(value==null || value.toString().isEmpty())
                return "sheet《座便器、小便器、妇洗器、柱盆》缺少表头信息，第"+ CellReference.convertNumToColString(cell.getColumnIndex())+"列";
        }
        return "";
    }
}
