package demo.service.excel.upload.create;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 如果上传的excel中有sheet，那就创建
 * 这种情况下，可以新增sheet
 */
public class AddSheetFactory implements ISheetCreateFactory {
    @Override
    public Sheet createSheet(XSSFWorkbook newExcel, Sheet lastestSheet, Sheet uploadSheet) {
        if(uploadSheet==null)
            return null;
        Sheet sheet = newExcel.createSheet(uploadSheet.getSheetName());
        return sheet;
    }
}
