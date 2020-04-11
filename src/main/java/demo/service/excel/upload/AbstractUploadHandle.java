package demo.service.excel.upload;

import demo.service.excel.upload.cell.ICellMerge;
import demo.service.excel.upload.comment.ICommentMerge;
import demo.service.excel.upload.create.ISheetCreateFactory;
import demo.service.excel.upload.picture.IPictureMerge;
import demo.util.ExcelUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * excel上传处理虚类
 */
public abstract class AbstractUploadHandle implements IUploadHandle {
    private ISheetCreateFactory sheetCreateFactory;     //sheet创建规则
    private ICellMerge cellMerge;                       //单元格合并规则
    private ICommentMerge commentMerge;                 //批注合并规则
    private IPictureMerge pictureMerge;                 //图片合并规则

    public AbstractUploadHandle(ISheetCreateFactory sheetCreateFactory,
                                ICellMerge cellMerge, ICommentMerge commentMerge, IPictureMerge pictureMerge){
        this.sheetCreateFactory = sheetCreateFactory;
        this.cellMerge = cellMerge;
        this.commentMerge = commentMerge;
        this.pictureMerge = pictureMerge;
    }

    /**
     * 统一的excel合并操作
     * @param principal
     * @param newExcel
     * @param lastestSheet
     * @param uploadSheet
     * @throws Exception
     */
    @Override
    public final void merge(AbstractAuthenticationToken principal, XSSFWorkbook newExcel, Sheet lastestSheet, Sheet uploadSheet) throws Exception {
        //在新的excel中生成新的sheet
        Sheet newSheet = sheetCreateFactory.createSheet(newExcel, lastestSheet, uploadSheet);
        if(newSheet==null)
            return;
        if(uploadSheet!=null){
            ExcelUtil.setSheetHidden(newSheet, ExcelUtil.isSheetHidden(uploadSheet));
        }else{
            ExcelUtil.setSheetHidden(newSheet, ExcelUtil.isSheetHidden(lastestSheet));
        }

        cellMerge.merge(principal, newSheet, lastestSheet, uploadSheet);
        commentMerge.merge(newSheet, lastestSheet, uploadSheet);
        pictureMerge.merge(principal, newSheet, lastestSheet, uploadSheet);
        ExcelUtil.copyRowHeight(uploadSheet, newSheet);
        ExcelUtil.copyColumnWidth(uploadSheet, newSheet);
        ExcelUtil.copyMergeRegion(uploadSheet, newSheet);
        return;
    }
}
