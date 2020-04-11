package demo.service.excel.upload.cell;

import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * 单元格合并操作
 */
public interface ICellMerge {
    void merge(AbstractAuthenticationToken principal, Sheet newSheet, Sheet lastestSheet, Sheet uploadSheet) throws Exception;
}
