package demo.service.excel.download;

import demo.util.ExcelUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * 默认的下载sheet处理类
 * 即默认具有所有数据的权限
 */
public class DefaultDownloadHandle implements IDownloadHandle {

    @Override
    public void handle(AbstractAuthenticationToken principal, XSSFSheet srcSheet, XSSFSheet dstSheet) {
        ExcelUtil.copySheet(srcSheet, dstSheet);
    }
}
