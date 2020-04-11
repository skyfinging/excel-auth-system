package demo.service.excel.upload.picture;

import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * 图片合并
 */
public interface IPictureMerge {
    void merge(AbstractAuthenticationToken principal, Sheet newSheet, Sheet lastestSheet, Sheet uploadSheet) throws Exception;
}
