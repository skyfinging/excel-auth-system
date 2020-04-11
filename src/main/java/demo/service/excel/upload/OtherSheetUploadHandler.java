package demo.service.excel.upload;

import demo.service.excel.upload.cell.TotalReplaceCellMerge;
import demo.service.excel.upload.comment.TotalReplaceCommentMerge;
import demo.service.excel.upload.create.AddSheetFactory;
import demo.service.excel.upload.picture.TotalReplacePictureMerge;

/**
 * 以上传的excel为准
 * 如果服务器上的excel中没有sheet，则创建，再拷贝
 * 如果服务器上的excel中有sheet，则拷贝
 */
public class OtherSheetUploadHandler extends AbstractUploadHandle {

    public OtherSheetUploadHandler() {
        super(new AddSheetFactory(),
                new TotalReplaceCellMerge(),
                new TotalReplaceCommentMerge(),
                new TotalReplacePictureMerge());
    }
}
