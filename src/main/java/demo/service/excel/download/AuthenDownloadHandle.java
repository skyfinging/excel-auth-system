package demo.service.excel.download;


import demo.service.excel.upload.cell.HeaderCellMerge;
import demo.service.authority.AuthorityBean;
import demo.service.authority.AuthorityService;
import demo.service.authority.HeadInfoBean;
import demo.util.AuthenticationUtil;
import demo.util.ExcelUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.security.authentication.AbstractAuthenticationToken;


/**
 * 下载文件的时候，需要过滤掉没有权限的数据
 * 根据表头来处理，如果用户没有该表头的权限，那么该列的数据不能提供下载，需要清空掉
 */
public class AuthenDownloadHandle implements IDownloadHandle {

    private AuthorityService authorityService;

    public AuthenDownloadHandle(AuthorityService authorityService){
        this.authorityService = authorityService;
    }

    @Override
    public void handle(AbstractAuthenticationToken principal, XSSFSheet srcSheet, XSSFSheet dstSheet) {
         //如果是admin权限，则具有整个sheet修改的权限，所以直接复制整个sheet即可
         if(AuthenticationUtil.authenRole(principal, "ROLE_ADMIN", null)){
             ExcelUtil.copySheet(srcSheet, dstSheet);
             return;
         }
         //获取该用户拥有权限的表头
         AuthorityBean excelHeads = authorityService.getColumnName(principal.getName());
         //获取表头信息
        HeadInfoBean headInfoBean = HeaderCellMerge.getHeadInfo(srcSheet);

         //拷贝行数据,要过滤掉没有权限的列
         srcSheet.rowIterator().forEachRemaining(row->copyRow(row,dstSheet,excelHeads,headInfoBean));

         //拷贝样式
         ExcelUtil.copyRowHeight(srcSheet, dstSheet);
         ExcelUtil.copyColumnWidth(srcSheet, dstSheet);
         ExcelUtil.copyMergeRegion(srcSheet, dstSheet);
         if(excelHeads.isAuthenInColumnName("图片"))    //如果有“图片”这一列的权限，则拷贝权限
             ExcelUtil.copyPicture(srcSheet, dstSheet);
         ExcelUtil.copyComment(srcSheet, dstSheet);
    }

    //遍历复制服务器上的excel的每一行，会过滤掉没有权限的列
    private void copyRow(Row srcRow, XSSFSheet dstSheet, AuthorityBean authorityBean, HeadInfoBean headInfoBean){
        Row dstRow = ExcelUtil.createRowIfNotExist(dstSheet, srcRow.getRowNum());
        srcRow.cellIterator().forEachRemaining(cell->copyCell(cell, dstRow, authorityBean, headInfoBean));
    }

    //每一列判断有没有权限，有权限的则复制单元格的值
    //没有权限的话，则把这一列设置为隐藏
    private void copyCell(Cell cell, Row dstRow, AuthorityBean authorityBean, HeadInfoBean headInfoBean){
        int index = cell.getColumnIndex();
        if(index>255)
            return;
        String headName =headInfoBean.getColumnNameAt(index);
        Cell dstCell = ExcelUtil.createCellIfNoExist(dstRow,index);
        if(cell.getRow().getRowNum()<2 || authorityBean.isAuthenInColumnName(headName)) {
            ExcelUtil.copyCellValue(cell, dstCell);
        }else{
            dstRow.getSheet().setColumnHidden(index,true);
        }
        ExcelUtil.copyCellStyle(cell, dstCell);
    }

}
