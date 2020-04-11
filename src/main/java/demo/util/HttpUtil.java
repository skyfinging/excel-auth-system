package demo.util;

import demo.view.page.BootstrapPage;
import demo.view.page.BootstrapPageItem;
import demo.view.page.IPageItemTransfer;
import demo.view.page.PageInfo;
import demo.service.query.IQuery;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class HttpUtil {
    private static String getRequestPayload(HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
        try(BufferedReader reader = req.getReader();) {
            char[]buff = new char[1024];
            int len;
            while((len = reader.read(buff)) != -1) {
                sb.append(buff,0, len);
            }
        }catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return sb.toString();
    }

    private static PageInfo getPageInfo(String json){
        PageInfo pageInfo = new PageInfo();
        JSONObject jsonObject = new JSONObject(json);
        Integer pageSize = jsonObject.getInt("pageSize");
        Integer pageIndex = jsonObject.getInt("pageIndex");
        if(pageSize==null)
            pageSize = 10;
        if(pageIndex==null)
            pageIndex = 0;
        pageInfo.setPageSize(pageSize);
        pageInfo.setPageIndex(pageIndex);
        return pageInfo;
    }

    private static BootstrapPage getBootstrapPage(List<BootstrapPageItem> items,int total){
        BootstrapPage page = new BootstrapPage();
        page.setRows(items);
        page.setTotal(total);
        return page;
    }

    public static <T> BootstrapPage tableAll(HttpServletRequest request, Sort.Order order, JpaRepository<T,?> jpaRepository, IPageItemTransfer<T> transfer){
        String json = getRequestPayload(request);
        PageInfo pageInfo = getPageInfo(json);

        Pageable pageRequest = PageRequest.of(pageInfo.getPageIndex(),pageInfo.getPageSize(), Sort.by(order));
        Page<T> list = jpaRepository.findAll(pageRequest);

        List<BootstrapPageItem> pageItemList =
                list.stream().map(lastFileInfo -> transfer.getBootstrapPageItem(list.getPageable(), lastFileInfo)).collect(Collectors.toList());

        BootstrapPage page = getBootstrapPage(pageItemList, (int) list.getTotalElements());
        return page;
    }

    public static <T> BootstrapPage searchAll(HttpServletRequest request, Sort.Order order, IQuery<T> iquery, IPageItemTransfer<T> transfer){
        String json = getRequestPayload(request);
        PageInfo pageInfo = getPageInfo(json);

        Pageable pageRequest = PageRequest.of(pageInfo.getPageIndex(),pageInfo.getPageSize(), Sort.by(order));
        Page<T> list = iquery.findAll(pageRequest,new JSONObject(json));

        List<BootstrapPageItem> pageItemList =
                list.stream().map(lastFileInfo -> transfer.getBootstrapPageItem(list.getPageable(), lastFileInfo)).collect(Collectors.toList());

        BootstrapPage page = getBootstrapPage(pageItemList, (int) list.getTotalElements());
        return page;
    }

}
