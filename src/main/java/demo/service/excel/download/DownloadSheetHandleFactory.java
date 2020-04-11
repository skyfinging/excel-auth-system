package demo.service.excel.download;

import demo.service.authority.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DownloadSheetHandleFactory {

    @Autowired
    private AuthorityService authorityService;

    public IDownloadHandle getSheetHandler(String sheetName){
        //只对特定的sheet进行权限控制
        if(sheetName.equals("座便器、小便器、妇洗器、柱盆"))
            return new AuthenDownloadHandle(authorityService);
        return new DefaultDownloadHandle();
    }
}
