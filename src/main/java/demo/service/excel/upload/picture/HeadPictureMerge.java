package demo.service.excel.upload.picture;

import demo.util.AuthenticationUtil;
import demo.util.ExcelUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Iterator;

/**
 * 图片复制
 * 如果没有图片这一列的权限，则最后excel的图片来源于原始的excel
 * 如果有图片这一列的权限，则最后excel的图片来源于上传的excel
 */
@Log4j2
public class HeadPictureMerge implements IPictureMerge {
    @Override
    public void merge(AbstractAuthenticationToken principal, Sheet newSheet, Sheet lastestSheet, Sheet uploadSheet) throws Exception {
        if(uploadSheet==null){
            ExcelUtil.copyPicture(lastestSheet, newSheet);
            return;
        }else if(lastestSheet==null){
            ExcelUtil.copyPicture(uploadSheet, newSheet);
            return;
        }
        if(AuthenticationUtil.authenRole(principal,"ROLE_ADMIN",null)){
            ExcelUtil.copyPicture(uploadSheet, newSheet);
            return;
        }

        if(AuthenticationUtil.authenRole(principal,"ROLE_COLUMN_PICTURE",null)) {
            int picutreColumn = getPictureColumnIndex(uploadSheet);
            if (picutreColumn == -1) {
                ExcelUtil.copyPicture(lastestSheet, newSheet);
                return;
            }
            ExcelUtil.copyPicture(uploadSheet, newSheet);
        }else{
            ExcelUtil.copyPicture(lastestSheet, newSheet);
        }
    }

    /**
     * 获取图片这一列的序号
     * @param sheet
     * @return
     * @throws Exception
     */
    private int getPictureColumnIndex(Sheet sheet) throws Exception {
        Row head = sheet.getRow(1);
        if(head==null)
            return -1;
        Iterator<Cell> iterator = head.iterator();
        while(iterator.hasNext()){
            XSSFCell cell = (XSSFCell) iterator.next();
            if("图片".equals(ExcelUtil.getMergedRegionValue(cell))) {
                int index = cell.getColumnIndex();
                return index;
            }
        }
        return -1;
    }
}
