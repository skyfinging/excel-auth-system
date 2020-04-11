package demo.service.excel.upload.create;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 上传的时候，合并之后的excel的sheet创建策略
 * 有多种情况：
 * 1.原始excel中有sheet，上传的excel没有sheet
 * 2.原始excel中有sheet，上传的excel有sheet
 * 3.原始的excel没有sheet，上传的excel有sheet
 */
public interface ISheetCreateFactory {
    Sheet createSheet(XSSFWorkbook newExcel, Sheet lastestSheet, Sheet uploadSheet);
}
