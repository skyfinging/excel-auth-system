package demo.service.excel.upload.cell;

import demo.util.ExcelUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * 完全拷贝单元格，以上传的excel为准
 */
public class TotalReplaceCellMerge implements ICellMerge {
    @Override
    public void merge(AbstractAuthenticationToken principal, Sheet newSheet, Sheet lastestSheet, Sheet uploadSheet) {
        if(uploadSheet!=null) {
            ExcelUtil.copyCell(uploadSheet, newSheet);
        }
    }
}
