package demo.service.excel.upload;

import demo.util.ExcelUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.File;

public class AuthorSheetMergeHandlerTest {
    @Test
    public void test() throws Exception {
        File file1 = new File("C:\\Users\\chenruijun\\Desktop\\新建文件夹 (2)\\1服务器上的最新文件.xlsx");
        File file2 = new File("C:\\Users\\chenruijun\\Desktop\\新建文件夹 (2)\\2下载修改.xlsx");
        AuthoritySheetUploadHandler authorSheetMergeHandler = new AuthoritySheetUploadHandler(null);
        XSSFWorkbook newExcel = new XSSFWorkbook();
        XSSFWorkbook excel1 = ExcelUtil.readExcel2007(file1);
        XSSFWorkbook excel2 = ExcelUtil.readExcel2007(file2);
        XSSFSheet sheet1 = excel1.getSheetAt(0);
        XSSFSheet sheet2 = excel2.getSheetAt(0);
        authorSheetMergeHandler.merge(null,newExcel,sheet1, sheet2);
        ExcelUtil.writeToFile(newExcel, new File("C:\\Users\\chenruijun\\Desktop\\new.xlsx"));
    }
}
