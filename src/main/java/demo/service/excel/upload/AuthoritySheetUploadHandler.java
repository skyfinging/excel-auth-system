package demo.service.excel.upload;

import demo.service.excel.upload.cell.HeaderCellMerge;
import demo.service.excel.upload.comment.TotalReplaceCommentMerge;
import demo.service.excel.upload.create.OldSheetFactory;
import demo.service.excel.upload.picture.HeadPictureMerge;
import demo.service.authority.AuthorityService;

/**
 * 需要进行权限管理的sheet
 * 目前适用于《座便器、小便器、妇洗器、柱盆》这张sheet
 */
public class AuthoritySheetUploadHandler extends AbstractUploadHandle {

    public AuthoritySheetUploadHandler(AuthorityService authorityService) {
        super(new OldSheetFactory(),
                new HeaderCellMerge(authorityService),
                new TotalReplaceCommentMerge(),
                new HeadPictureMerge());
    }
}
