package demo.service.excel.upload.create;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 如果原来服务器上的excel中没有sheet，那就没有，如果有，那就有
 * 这种情况下，上传的excel中人为添加新的sheet将会被忽略掉
 */
public class OldSheetFactory implements ISheetCreateFactory {
    @Override
    public Sheet createSheet(XSSFWorkbook newExcel, Sheet lastestSheet, Sheet uploadSheet) {
        if(lastestSheet==null)
            return null;
        Sheet sheet = newExcel.createSheet(lastestSheet.getSheetName());
        return sheet;
    }
}
