package demo.service.excel.download;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * excel下载的时候，进行权限过滤处理
 */
public interface IDownloadHandle {
    void handle(AbstractAuthenticationToken principal, XSSFSheet srcSheet, XSSFSheet dstSheet);
}
