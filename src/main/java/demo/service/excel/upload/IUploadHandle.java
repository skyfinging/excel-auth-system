package demo.service.excel.upload;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * sheet合并操作，也是上传excel的时候的操作
 */
public interface IUploadHandle {
    void merge(AbstractAuthenticationToken principal, XSSFWorkbook newExcel, Sheet lastestSheet, Sheet uploadSheet) throws Exception;
}
