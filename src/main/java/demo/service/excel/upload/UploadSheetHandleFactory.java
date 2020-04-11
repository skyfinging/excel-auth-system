package demo.service.excel.upload;

import demo.service.authority.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UploadSheetHandleFactory {
    @Autowired
    private AuthorityService authorityService;

    public IUploadHandle getSheetMergeHandler(String sheetName){
        if(sheetName.equals("座便器、小便器、妇洗器、柱盆"))
            return new AuthoritySheetUploadHandler(authorityService);
        return new OtherSheetUploadHandler();
    }

    public IUploadHandle getAddSheetMergeHandler(){
        return null;
    }

    public IUploadHandle getRemoveSheetMergeHandler(){
        return null;
    }
}
