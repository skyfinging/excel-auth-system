package demo.view.page;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 适配bootstrap的表格控件的分页功能
 * rows和total名称不能变
 */
@Getter
@Setter
public class BootstrapPage {
    private List<BootstrapPageItem> rows;
    private int total;
}
