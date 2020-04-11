package demo.service.excel.upload.cell;

import demo.service.authority.AuthorityBean;
import demo.service.authority.AuthorityService;
import demo.service.authority.HeadInfoBean;
import demo.util.AuthenticationUtil;
import demo.util.ExcelUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.*;

/**
 * 需要进行权限控制的sheet合并操作
 * 根据表头来处理
 */
public class HeaderCellMerge implements ICellMerge {
    private AuthorityService authorityService;

    public HeaderCellMerge(AuthorityService authorityService){
        this.authorityService = authorityService;
    }

    @Override
    public void merge(AbstractAuthenticationToken principal, Sheet newSheet, Sheet lastestSheet, Sheet uploadSheet) throws Exception {
        if(uploadSheet==null){
            ExcelUtil.copyCell(lastestSheet, newSheet);
            return;
        }else if(lastestSheet==null){
            ExcelUtil.copyCell(uploadSheet, newSheet);
            return;
        }
        //如果是admin，则直接复制即可
        if(AuthenticationUtil.authenRole(principal, "ROLE_ADMIN", null)){
            ExcelUtil.copyCell(uploadSheet, newSheet);
            return;
        }
        //初始化表头信息，判断上传的excel有没有修改过表头
        HeadInfoBean lastestHead = getHeadInfo(lastestSheet);
        HeadInfoBean uploadHead = getHeadInfo(uploadSheet);
        boolean isModifyHead = isModifyHead(lastestHead, uploadHead);
        if(isModifyHead==false)
            throw new Exception("不能修改表头信息");

        //不能增删数据，因为可能有的列没有权限，如果进行增删操作，会导致没有权限的行对不上
        int rowNum = lastestSheet.getPhysicalNumberOfRows();
        int rowNum1 = uploadSheet.getPhysicalNumberOfRows();
        if(rowNum!=rowNum1){
            throw new Exception("不能增删数据");
        }

        //获取该用户拥有权限的表头
        AuthorityBean excelHeads = authorityService.getColumnName(principal.getName());

        //先复制服务器的excel作为基础excel
        lastestSheet.rowIterator().forEachRemaining(
                row-> ExcelUtil.copyRow(row, ExcelUtil.createRowIfNotExist(newSheet, row.getRowNum())));
        //再复制上传的excel数据，没有权限的列不会复制
        uploadSheet.rowIterator().forEachRemaining(
                row-> copyUploadRow(row,newSheet.getRow(row.getRowNum()), uploadHead, excelHeads));
    }

    private void copyUploadRow(Row uploadRow, Row newRow, HeadInfoBean uploadHead, AuthorityBean authorityBean){
        //前两行不用复制，是表头
        if(uploadRow.getRowNum()<2)
            return;
        uploadRow.cellIterator().forEachRemaining(
                cell-> copyUploadCell(cell, newRow.getCell(cell.getColumnIndex()), uploadHead, authorityBean));
    }

    private void copyUploadCell(Cell uploadCell, Cell newCell, HeadInfoBean uploadHead, AuthorityBean authorityBean){
        int index = uploadCell.getColumnIndex();
        if(index>255)
            return;
        String headName = uploadHead.getColumnNameAt(index);
        //如果没有权限，则不用复制
        if(headName==null || headName.isEmpty() || !authorityBean.isAuthenInColumnName(headName))
            return;
        ExcelUtil.copyCellValue(uploadCell, newCell);
        ExcelUtil.copyCellStyle(uploadCell, newCell);
    }

    /**
     * 获取表头信息
     * @param sheet
     * @return
     */
    public static HeadInfoBean getHeadInfo(Sheet sheet) {
        LinkedHashMap<Integer, String> headMap = new LinkedHashMap<>();
        Row head2 = sheet.getRow(1);
        if(head2==null)
            return new HeadInfoBean(headMap);
        Iterator<Cell> iterator = head2.iterator();
        while(iterator.hasNext()){
            XSSFCell cell = (XSSFCell) iterator.next();
            Object value = ExcelUtil.getMergedRegionValue(cell);
            if(value==null)
                headMap.put(cell.getColumnIndex(), "");
            else
                headMap.put(cell.getColumnIndex(), value.toString());
        }
        return new HeadInfoBean(headMap);
    }

    private boolean isModifyHead(HeadInfoBean lastestHead, HeadInfoBean uploadHead){
        if(uploadHead.isEmpty())
            return true;
        if(lastestHead.size()<uploadHead.size())
            return false;
        //上传的excel的表头不能少
        Iterator<Integer> iterator = uploadHead.iterator();
        while(iterator.hasNext()){
            Integer columnIndex = iterator.next();
            String headName = uploadHead.getColumnNameAt(columnIndex);
            String headName1 = lastestHead.getColumnNameAt(columnIndex);
            if(!headName.equals(headName1)){
                return false;
            }
        }
        return true;
    }
}
