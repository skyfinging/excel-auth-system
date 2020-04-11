package demo.service.excel.upload.picture;

import demo.util.ExcelUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * 直接将excel中的图片更新为上传的excel中的图片
 */
public class TotalReplacePictureMerge implements IPictureMerge {
    @Override
    public void merge(AbstractAuthenticationToken principal, Sheet newSheet, Sheet lastestSheet, Sheet uploadSheet) {
        if(uploadSheet!=null)
            ExcelUtil.copyPicture(uploadSheet, newSheet);
    }
}
