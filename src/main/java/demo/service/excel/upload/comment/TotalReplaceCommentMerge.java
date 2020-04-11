package demo.service.excel.upload.comment;

import demo.util.ExcelUtil;
import org.apache.poi.ss.usermodel.Sheet;

public class TotalReplaceCommentMerge implements ICommentMerge {
    @Override
    public void merge(Sheet newSheet, Sheet lastestSheet, Sheet uploadSheet) {
        if(uploadSheet!=null)
            ExcelUtil.copyComment(uploadSheet, newSheet);
    }
}
