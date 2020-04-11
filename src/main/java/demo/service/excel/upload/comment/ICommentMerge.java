package demo.service.excel.upload.comment;

import org.apache.poi.ss.usermodel.Sheet;

/**
 * 批注合并
 * 一般是直接拷贝即可，目前批注不做权限管理
 */
public interface ICommentMerge {
    void merge(Sheet newSheet, Sheet lastestSheet, Sheet uploadSheet);
}
