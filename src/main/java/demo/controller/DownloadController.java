package demo.controller;

import demo.config.aop.AuthenticRequire;
import demo.config.aop.ResponseEntityReturnHandle;
import demo.dao.LastFileInfoRepository;
import demo.dao.bean.LastFileInfo;
import demo.service.excel.download.IDownloadHandle;
import demo.service.excel.download.DownloadSheetHandleFactory;
import demo.service.FileService;
import demo.service.IdGenServices;
import demo.service.IdGenEnum;
import demo.util.AESUtil;
import demo.util.ExcelUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件下载请求
 */
@Log4j2
@Controller
public class DownloadController {

    @Autowired
    private FileService fileService;

    @Autowired
    private IdGenServices idGenServices;

    @Autowired
    private LastFileInfoRepository lastFileInfoRepository;

    @Autowired
    private DownloadSheetHandleFactory downloadSheetHandleFactory;

    //声明该方法的执行需要ROLE_DOWNLOAD权限
    @GetMapping("/download")
    @AuthenticRequire(value = "ROLE_DOWNLOAD",handle = ResponseEntityReturnHandle.class)
    public ResponseEntity<InputStreamResource> downloadFile1(HttpServletRequest request) throws IOException {
        Map<String, Object> result = new HashMap<>();
        //准备工作
        fileService.preparedDir(IdGenEnum.File);
        XSSFWorkbook download = null;
        XSSFWorkbook newExcel = null;
        InputStream is = null;
        try {
            IdGenEnum.File.getReadWriteLock().readLock().lock();
            //第1步，获取文件当前版本号
            long id = idGenServices.getCurrentId(IdGenEnum.File);
            //第2步，解析当前版本最新文件为Excel格式
            File file = fileService.getLastestFile(IdGenEnum.File, id + "");
            try {
                download = ExcelUtil.readExcel2007(file);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                result.put("error", "无法解析当前最新版本文件");
                return ResponseEntity.ok().contentLength(0).body(null);
            }
            //第3步，将当前版本号写入excel文件中
            String version = AESUtil.encode(id + "");
            //第4步，校验权限，去掉没有权限的数据
            newExcel = checkAuthen(download, (AbstractAuthenticationToken) request.getUserPrincipal());
            //第5步，把版本号写入新的excel中
            createVersionSheet(newExcel, version);
            //第6步，查询当前版本的原始文件名称
            LastFileInfo lastFileInfo = lastFileInfoRepository.findByVersion(id);
            String fileName = parseCNFileName(lastFileInfo.getFileName());
            //第7步，下载文件
            is = ExcelUtil.writeToStream(newExcel);
            //最后，下载文件
            long length = is.available();
            InputStreamResource resource = new InputStreamResource(is);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(length)
                    .body(resource);
        }finally {
            IdGenEnum.File.getReadWriteLock().readLock().unlock();
            if(download!=null)
                download.close();
            if(newExcel!=null)
                newExcel.close();
            if(is!=null)
                is.close();
        }
    }

    /**
     * 创建version这张表，并设置为隐藏
     * @param wb
     * @param version
     */
    private void createVersionSheet(XSSFWorkbook wb, String version){
        XSSFSheet sheet = wb.getSheet("Version");
        if(sheet==null)
            sheet = wb.createSheet("Version");
        XSSFRow row = sheet.getRow(0);
        if(row == null)
            row = sheet.createRow(0);
        XSSFCell cell = row.getCell(0);
        if(cell==null)
            cell = row.createCell(0);
        cell.setCellValue(version);

        ExcelUtil.setSheetHidden(sheet,true);
    }

    /**
     * 文件名称转义，处理掉文件名称中的中文字符
     * @param fileName
     * @return
     */
    private String parseCNFileName(String fileName){
        String newFileName = fileName;
        try {
            newFileName = URLEncoder.encode(fileName,"UTF-8");
        }catch (UnsupportedEncodingException e){
            log.warn("文件名字转换异常,"+fileName);
        }
        return newFileName;
    }

    /**
     * 创建新的excel表格，新的excel表格中去掉了没有权限的数据
     * 没有权限的数据列，表头还在，但是数据为空，而且隐藏起来
     * @param wb
     * @param principal
     * @return
     */
    private XSSFWorkbook checkAuthen(XSSFWorkbook wb, AbstractAuthenticationToken principal){
        XSSFWorkbook newExcel = new XSSFWorkbook();
        int sheets = wb.getNumberOfSheets();
        for(int i=0;i<sheets;i++) {
            XSSFSheet sheet = wb.getSheetAt(i);
            XSSFSheet sheet2 = newExcel.createSheet(sheet.getSheetName());
            newExcel.setSheetHidden(i,wb.isSheetHidden(i));
            //根据sheet名称，找到对应的权限处理类
            IDownloadHandle downloadHandle = downloadSheetHandleFactory.getSheetHandler(sheet.getSheetName());
            if(downloadHandle!=null) {
                downloadHandle.handle(principal, sheet, sheet2);
            }
        }
        //excel复制完成之后，需要清除样式缓存
        ExcelUtil.clearStyleCache();
        return newExcel;
    }

}
