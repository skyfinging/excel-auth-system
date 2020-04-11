package demo.util;


import lombok.extern.log4j.Log4j2;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.model.CommentsTable;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 提供Excel操作
 */
@Log4j2
public class ExcelUtil {
    //临时存放excel的样式，两个不同的excel，就算样式相同，也不能直接使用同一个XSSFCellStyle
    private static ThreadLocal<Map<CellStyle, CellStyle>> styleCache = new ThreadLocal<>();
    /**
     * 一个excel操作完成之后，要记得清理样式缓存，否则可能内存溢出
     */
    public static void clearStyleCache(){
        Map map = styleCache.get();
        if(map!=null)
            map.clear();
    }

    /**
     * 将文件读取成为Excel2007格式
     * @param file
     * @return
     * @throws InvalidFormatException
     * @throws IOException
     */
    public static XSSFWorkbook readExcel2007(File file) throws InvalidFormatException, IOException {
        ZipSecureFile.setMinInflateRatio(-1.0d);        //防止打开excel失败
        OPCPackage p = OPCPackage.open(file);
        XSSFWorkbook xwb = new XSSFWorkbook(p);
        return xwb;
    }

    /**
     * 把excel对象写入文件
     * @param xwb
     * @param file
     * @throws IOException
     */
    public static void writeToFile(XSSFWorkbook xwb, File file) throws IOException {
        try(OutputStream ous = new FileOutputStream(file)){
            xwb.write(ous);
        }
    }

    /**
     * 把excel对象写入流中，并返回流对象
     * @param xwb
     * @return
     */
    public static InputStream writeToStream(XSSFWorkbook xwb){
        try(ByteArrayOutputStream ous = new ByteArrayOutputStream()){
            xwb.write(ous);
            try(ByteArrayInputStream is = new ByteArrayInputStream(ous.toByteArray())){
                return is;
            }
        }catch (IOException e){
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 拷贝excel文件到另一个文件
     * @param srcFile
     * @param dstFile
     * @throws IOException
     * @throws InvalidFormatException
     */
    public static void copyExcel(File srcFile, File dstFile) throws IOException, InvalidFormatException {
        XSSFWorkbook xwb = ExcelUtil.readExcel2007(srcFile);
        XSSFWorkbook xwb2 = new XSSFWorkbook();
        int sheets = xwb.getNumberOfSheets();
        for(int i=0;i<sheets;i++) {
            XSSFSheet sheet = xwb.getSheetAt(i);
            XSSFSheet sheet2 = xwb2.createSheet(sheet.getSheetName());
            xwb2.setSheetHidden(i,xwb.isSheetHidden(i));
            ExcelUtil.copySheet(sheet, sheet2);
        }
        copyTheme(xwb, xwb2);
        xwb.close();
        ExcelUtil.clearStyleCache();
        ExcelUtil.writeToFile(xwb2, dstFile);
        xwb2.close();
    }

    /**
     * 拷贝主题，有时候样式相同，但是颜色不同，就是因为主题不同
     * 拷贝无效，不知道怎么拷贝主题
     * @param srcWb
     * @param dstWb
     */
    public static void copyTheme(XSSFWorkbook srcWb, XSSFWorkbook dstWb){
//        StylesTable stylesTable = srcWb.getStylesSource();
//        StylesTable stylesTable1 = dstWb.getStylesSource();
//        ThemesTable themesTable = stylesTable.getTheme();
//        stylesTable1.setTheme(themesTable);

    }

    /**
     * 拷贝sheet,包括样式，图片，批注
     * @param srcSheet 数据源，被拷贝的sheet
     * @param dstSheet 空sheet
     */
    public static void copySheet(XSSFSheet srcSheet, XSSFSheet dstSheet){
        ExcelUtil.copyCell(srcSheet, dstSheet);
        ExcelUtil.copyPicture(srcSheet, dstSheet);
        ExcelUtil.copyComment(srcSheet, dstSheet);
        ExcelUtil.copyRowHeight(srcSheet, dstSheet);
        ExcelUtil.copyColumnWidth(srcSheet, dstSheet);
        ExcelUtil.copyMergeRegion(srcSheet, dstSheet);
    }

    /**
     * 将整个sheet的单元格拷贝到另一个sheet中
     * @param srcSheet 数据源，被拷贝的sheet
     * @param dstSheet 空sheet
     */
    public static void copyCell(Sheet srcSheet, Sheet dstSheet){
        //拷贝单元格的值
        srcSheet.rowIterator().forEachRemaining(
                row-> copyRow(row, dstSheet.createRow(row.getRowNum())));
    }

    /**
     * 拷贝行
     * @param srcRow 数据源，被拷贝的行
     * @param dstRow 空行
     */
    public static void copyRow(Row srcRow, Row dstRow){
        srcRow.cellIterator().forEachRemaining(
                cell->copyCell(cell, dstRow));
    }

    /**
     * 拷贝单元格，包括值和样式
     * @param srcCell
     * @param dstRow
     */
    public static void copyCell(Cell srcCell, Row dstRow){
        int index = srcCell.getColumnIndex();
        if(index>255)
            return;
        Cell dstCell = createCellIfNoExist(dstRow, index);
        copyCellValue(srcCell, dstCell);
        copyCellStyle(srcCell, dstCell);
    }

    /**
     * 拷贝单元格，包括值和样式
     * @param srcCell
     * @param dstCell
     */
    public static void copyCell(Cell srcCell, Cell dstCell){
        int index = srcCell.getColumnIndex();
        if(index>255)
            return;
        copyCellValue(srcCell, dstCell);
        copyCellStyle(srcCell, dstCell);
    }

    /**
     * 拷贝单元格的值
     * @param srcCell 数据源，被拷贝的单元格
     * @param dstCell 空单元格
     */
    public static void copyCellValue(Cell srcCell, Cell dstCell){
        if(srcCell==null)
            return;
        CellType cellType = srcCell.getCellType();
        dstCell.setCellType(cellType);
        switch(cellType){
            case _NONE:
            case BLANK:
                return;
            case STRING:
                dstCell.setCellValue(srcCell.getStringCellValue());
                return;
            case FORMULA:
                dstCell.setCellFormula(srcCell.getCellFormula());
                return;
            case NUMERIC:
                dstCell.setCellValue(srcCell.getNumericCellValue());
                return;
            case BOOLEAN:
                dstCell.setCellValue(srcCell.getBooleanCellValue());
                return;
            case ERROR:
                dstCell.setCellValue(srcCell.getErrorCellValue());
                return;
        }
        return ;
    }

    /**
     * 拷贝单元格的样式
     * @param srcCell 数据源，被拷贝的单元格
     * @param dstCell 空单元格
     */
    public static void copyCellStyle(Cell srcCell, Cell dstCell){
        Workbook wb = srcCell.getRow().getSheet().getWorkbook();
        Map<CellStyle, CellStyle> styleMap = styleCache.get();
        if(styleMap==null){
            styleMap = new HashMap<>();
            styleCache.set(styleMap);
        }
        CellStyle style = srcCell.getCellStyle();
        CellStyle style2 = styleMap.get(style);
        if(style2==null){
            style2 = dstCell.getRow().getSheet().getWorkbook().createCellStyle();
            style2.cloneStyleFrom(style);
            styleMap.put(style, style2);
        }
        dstCell.setCellStyle(style2);
    }

    /**
     * 拷贝sheet的批注
     * @param srcSheet
     * @param dstSheet
     */
    public static void copyComment(Sheet srcSheet, Sheet dstSheet){
        if(!(srcSheet instanceof XSSFSheet))
            return;
        XSSFSheet xssfSheet = (XSSFSheet) srcSheet;
        for(POIXMLDocumentPart each : xssfSheet.getRelations()){
            if(each instanceof CommentsTable){
                CommentsTable commentsTable = (CommentsTable) each;
                Drawing patriarch = dstSheet.getDrawingPatriarch();
                if(patriarch==null){
                    patriarch = dstSheet.createDrawingPatriarch();
                }
                Iterator<CellAddress> address = commentsTable.getCellAddresses();
                while(address.hasNext()){
                    CellAddress cellAddress = address.next();
                    XSSFComment comment = commentsTable.findCellComment(cellAddress);
                    ClientAnchor anchor = comment.getClientAnchor();
                    if(anchor==null){
                        anchor = new XSSFClientAnchor();
                    }
                    Comment comment1 = patriarch.createCellComment(anchor);
                    comment1.setAddress(cellAddress);
                    comment1.setString(comment.getString());
                    comment1.setVisible(comment.isVisible());
                }
                break;
            }
        }
    }

    /**
     * 拷贝sheet中的图片
     * @param srcSheet
     * @param dstSheet
     */
    public static void copyPicture(Sheet srcSheet, Sheet dstSheet){
        XSSFDrawing drawing = getDrawingFromSheet(srcSheet);
        if(drawing==null)
            return;
        List<XSSFShape> shapes = drawing.getShapes();
        if(shapes==null || shapes.isEmpty())
            return;

        Drawing patriarch = dstSheet.getDrawingPatriarch();
        if(patriarch==null)
            patriarch = dstSheet.createDrawingPatriarch();
        for (XSSFShape shape : shapes) {
            XSSFPicture pic = (XSSFPicture) shape;
            try {
                XSSFClientAnchor anchor = getClientAnchor(pic);
                int index = dstSheet.getWorkbook().addPicture(pic.getPictureData().getData(), XSSFWorkbook.PICTURE_TYPE_BMP);
                patriarch.createPicture(anchor,index);
            } catch (Exception e) {
                log.warn("复制图片失败："+e);
                continue;
            }
        }
    }

    /**
     * 拷贝sheet中的合并单元格信息
     * @param srcSheet
     * @param dstSheet
     */
    public static void copyMergeRegion(Sheet srcSheet, Sheet dstSheet){
        int regions = srcSheet.getNumMergedRegions();
        for(int i=0;i<regions;i++){
            CellRangeAddress range = srcSheet.getMergedRegion(i);
            dstSheet.addMergedRegion(range);
        }
    }

    /**
     * 拷贝sheet的行高
     * @param srcSheet
     * @param DstSheet
     */
    public static void copyRowHeight(Sheet srcSheet, Sheet DstSheet){
        Iterator<Row> rowIterator = srcSheet.rowIterator();
        while(rowIterator.hasNext()){
            XSSFRow row = (XSSFRow) rowIterator.next();
            int rowIndex = row.getRowNum();
            Row row2 = DstSheet.getRow(rowIndex);
            int height = row.getHeight();
            float point = row.getHeightInPoints();
            row2.setHeight((short) height);
            row2.setHeightInPoints(point);
        }
    }

    /**
     * 拷贝sheet的列宽度
     * @param srcSheet
     * @param DstSheet
     */
    public static void copyColumnWidth(Sheet srcSheet, Sheet DstSheet){
        int rows = srcSheet.getPhysicalNumberOfRows();
        if(rows<1)
            return;
        //设置单元格宽度
        int columns = srcSheet.getRow(srcSheet.getFirstRowNum()).getPhysicalNumberOfCells();
        for(int i=0;i<columns;i++){
            int width = srcSheet.getColumnWidth(i);
            DstSheet.setColumnWidth(i, width);
        }
    }

    /**
     * 如果sheet中不存在row，则创建
     * @param sheet
     * @param rowIndex
     * @return
     */
    public static Row createRowIfNotExist(Sheet sheet, int rowIndex){
        Row dstRow = sheet.getRow(rowIndex);
        if(dstRow==null)
            dstRow = sheet.createRow(rowIndex);
        return dstRow;
    }

    /**
     * 如果Row中不存在cell，则创建
     * @param row
     * @param cellIndex
     * @return
     */
    public static Cell createCellIfNoExist(Row row, int cellIndex){
        Cell cell = row.getCell(cellIndex);
        if(cell==null)
            cell = row.createCell(cellIndex);
        return cell;
    }

    /**
     * 获取sheet中的绘图对象
     * 绘图对象中可以提取图片信息
     * @param sheet
     * @return
     */
    public static XSSFDrawing getDrawingFromSheet(Sheet sheet) {
        if(sheet instanceof XSSFSheet) {
            XSSFSheet xssfSheet = (XSSFSheet) sheet;
            for (POIXMLDocumentPart each : xssfSheet.getRelations()) {
                if (each instanceof XSSFDrawing) {
                    return (XSSFDrawing) each;
                }
            }
        }
        return null;
    }

    /**
     * 获取图片的位置信息
     * @param picture
     * @return
     */
    public static XSSFClientAnchor getClientAnchor(XSSFPicture picture){
        XSSFClientAnchor anchor =  picture.getClientAnchor();
        if(anchor.getDy2()<anchor.getDy1()){
            int temp = anchor.getDy1();
            anchor.setDy1(anchor.getDy2());
            anchor.setDy2(temp);
        }
        if(anchor.getDx2()<anchor.getDx1()){
            int temp = anchor.getDx1();
            anchor.setDx1(anchor.getDx2());
            anchor.setDx2(temp);
        }
        return anchor;
    }

    /**
     * 获取单元格的值
     * @param cell
     * @return
     */
    public static Object getCellValue(Cell cell) {
        CellType cellType = cell.getCellType();
        switch(cellType){
            case _NONE:
            case BLANK:
                return null;
            case STRING:
                return cell.getStringCellValue();
            case FORMULA:
                return cell.getCellFormula();
            case NUMERIC:
                return cell.getNumericCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case ERROR:
                return cell.getErrorCellValue();
        }
        return null;
    }

    /**
     * 获取单元格的值，该单元格可能是合并的单元格
     * @param cell
     * @return
     */
    public static Object getMergedRegionValue(Cell cell) {
        Object value = getCellValue(cell);
        if(value!=null)
            return value;
        Sheet sheet = cell.getSheet();
        int row = cell.getRowIndex();
        int column = cell.getColumnIndex();
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress ca = sheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();
            if (row >= firstRow && row <= lastRow
                    && column >= firstColumn && column <= lastColumn) {
                    Row fRow = sheet.getRow(firstRow);
                    Cell fCell = fRow.getCell(firstColumn);
                    return getCellValue(fCell);
            }
        }
        return null;
    }

    /**
     * 判断该sheet是不是隐藏的
     * @param sheet
     * @return
     */
    public static boolean isSheetHidden(Sheet sheet){
        int index = sheet.getWorkbook().getSheetIndex(sheet.getSheetName());
        return sheet.getWorkbook().isSheetHidden(index);
    }

    /**
     * 设置sheet的隐藏性
     * @param sheet
     * @param isHidden
     */
    public static void setSheetHidden(Sheet sheet, boolean isHidden){
        int index = sheet.getWorkbook().getSheetIndex(sheet.getSheetName());
        sheet.getWorkbook().setSheetHidden(index, isHidden);
    }

}
