package demo.util;

import com.sun.javafx.scene.layout.region.Margins;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class ExcelUtilTest {
    @Test
    public void test() throws IOException, InvalidFormatException {
        File file = new File("C:\\Users\\chenruijun\\Desktop\\新建文件夹 (2)\\5修改之后.xlsx");
        File file2 = new File("C:\\Users\\chenruijun\\Desktop\\new.xlsx");


        ExcelUtil.copyExcel(file, file2);
    }

    @Test
    public void test1() throws IOException, InvalidFormatException {
//        File file = new File("C:\\Users\\chenruijun\\Desktop\\新建文件夹 (2)\\3更新之后.xlsx");
        File file = new File("E:\\temp\\File\\lastest\\63");
        LocalDateTime begin = LocalDateTime.now();
        XSSFWorkbook wb = ExcelUtil.readExcel2007(file);
        LocalDateTime end = LocalDateTime.now();
        System.out.println(Duration.between(begin, end).getSeconds());
    }
}
