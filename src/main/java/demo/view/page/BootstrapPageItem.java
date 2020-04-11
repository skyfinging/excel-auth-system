package demo.view.page;

import lombok.Getter;
import lombok.Setter;

/**
 * 适配bootstrap表格控件的分页功能
 * 成员变量名称不能变
 */
@Getter
@Setter
public abstract class BootstrapPageItem {
    private int pageSize;
    private int page;
    private int offset;
}
