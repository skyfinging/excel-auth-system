package demo.controller;

import demo.view.page.*;
import demo.dao.ExcelHeadRepository;
import demo.dao.bean.ExcelHead;
import demo.service.query.ExcelHeadQuery;
import demo.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * excel表头管理
 */
@Controller
public class HeadController {

    @Autowired
    private ExcelHeadRepository headRepository;

    @RequestMapping("getHeadList")
    @ResponseBody
    public BootstrapPage getHeadList(HttpServletRequest request){
        BootstrapPage page = HttpUtil.searchAll(request,
                Sort.Order.asc("id"), new ExcelHeadQuery(headRepository), new ExcelPageItemTransfer());
        return page;
    }

    @RequestMapping("updateExcelHead")
    public ModelAndView updateExcelHead(HttpServletRequest request, RedirectAttributes attr){
        ModelAndView view = new ModelAndView();
        view.setViewName("redirect:/tabTableHead");
        boolean success = false;
        Integer id = Integer.parseInt(request.getParameter("update_id"));
        String name = request.getParameter("update_name");
        Optional<ExcelHead> excelHeadOptional = headRepository.findById(id);
        if(excelHeadOptional.isPresent()){
            ExcelHead excelHead = excelHeadOptional.get();
            if(name!=null && name.isEmpty()==false) {
                String oldName = excelHead.getName();
                excelHead.setName(name);
                headRepository.save(excelHead);
                excelHeadOptional = headRepository.findById(id);
                if(excelHeadOptional.isPresent()){
                    String newName = excelHeadOptional.get().getName();
                    if(newName.equals(name)){
                        attr.addFlashAttribute("notice","修改成功["+oldName+"]->["+newName+"]");
                        success = true;
                    }
                }
            }
        }
        if(!success)
            attr.addFlashAttribute("notice","修改["+Optional.ofNullable(name).orElse("")+"]失败");

        return view;
    }

    @RequestMapping("addExcelHead")
    public ModelAndView addExcelHead(HttpServletRequest request, RedirectAttributes attr){
        ModelAndView view = new ModelAndView();
        view.setViewName("redirect:/tabTableHead");
        boolean success = false;
        String name = request.getParameter("add_name");
        if(name!=null && !name.isEmpty()){
            Integer maxId = headRepository.getMaxId();
            ExcelHead head = new ExcelHead(maxId+1);
            head.setName(name);
            headRepository.save(head);
            Optional<ExcelHead> headOptional = headRepository.findById(head.getId());
            if(headOptional.isPresent()){
                success = true;
                attr.addFlashAttribute("notice","添加["+name+"]成功");
            }
        }
        if(!success)
            attr.addFlashAttribute("notice","添加["+Optional.ofNullable(name).orElse("")+"]失败");

        return view;
    }

    @RequestMapping("delHead")
    @Transactional
    public ModelAndView delHead(HttpServletRequest request, RedirectAttributes attr){
        ModelAndView view = new ModelAndView();
        view.setViewName("redirect:/tabTableHead");
        Integer id = Integer.parseInt(request.getParameter("id"));
        Optional<ExcelHead> head = headRepository.findById(id);
        boolean success = false;
        String name = "";
        if(head.isPresent()) {
            name = head.get().getName();
            headRepository.deleteAllUserHeadsById(id);
            headRepository.deleteById(id);
            head = headRepository.findById(id);
            if(!head.isPresent()) {
                success = true;
                attr.addFlashAttribute("notice","删除["+name+"]成功");
            }
        }
        if(!success)
            attr.addFlashAttribute("notice", "删除[" + name + "]失败");
        return view;
    }
}

class ExcelPageItemTransfer implements IPageItemTransfer<ExcelHead> {
    @Override
    public BootstrapPageItem getBootstrapPageItem(Pageable pageable, ExcelHead item) {
        ExcelHeadItem excelHeadItem = new ExcelHeadItem();
        excelHeadItem.setId(item.getId());
        excelHeadItem.setName(item.getName());

        excelHeadItem.setPage(pageable.getPageNumber());
        excelHeadItem.setPageSize(pageable.getPageSize());
        excelHeadItem.setOffset((int) pageable.getOffset());
        return excelHeadItem;
    }
}
