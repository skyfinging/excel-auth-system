package demo.controller;

import demo.view.page.*;
import demo.dao.LastFileInfoRepository;
import demo.dao.bean.LastFileInfo;
import demo.util.HttpUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Log4j2
@Controller
public class UploadLogController {
    @Autowired
    private LastFileInfoRepository lastFileInfoRepository;

    @RequestMapping("/getUploadLog")
    @ResponseBody
    public BootstrapPage getUploadLog(HttpServletRequest request){
        BootstrapPage page = HttpUtil.tableAll(request,
                Sort.Order.desc("version"), lastFileInfoRepository, new UploadInfoPageItemTransfer());
        return page;
    }

}

class UploadInfoPageItemTransfer implements IPageItemTransfer<LastFileInfo>{

    @Override
    public BootstrapPageItem getBootstrapPageItem(Pageable pageable, LastFileInfo lastFileInfo) {
        UploadInfoTableItem uploadInfoTableItem = new UploadInfoTableItem();
        uploadInfoTableItem.setFileName(lastFileInfo.getFileName());
        uploadInfoTableItem.setId(lastFileInfo.getId());
        uploadInfoTableItem.setFileSize(lastFileInfo.getFileSize());
        uploadInfoTableItem.setVersion(lastFileInfo.getVersion());
        uploadInfoTableItem.setUploadTime(lastFileInfo.getUploadTime());
        uploadInfoTableItem.setUserName(lastFileInfo.getUserName());

        uploadInfoTableItem.setPage(pageable.getPageNumber());
        uploadInfoTableItem.setPageSize(pageable.getPageSize());
        uploadInfoTableItem.setOffset((int) pageable.getOffset());
        return uploadInfoTableItem;
    }
}
