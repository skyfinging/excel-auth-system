package demo.controller;

import demo.config.aop.AuthenticRequire;
import demo.config.aop.ModelAndViewReturnHandle;
import demo.view.page.*;
import demo.dao.SysUserRepository;
import demo.dao.bean.ExcelHead;
import demo.dao.bean.SysRole;
import demo.dao.bean.SysUser;
import demo.service.query.SysUserQuery;
import demo.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class UserController {

    @Autowired
    private SysUserRepository sysUserRepository;

    @RequestMapping("/updatePassword")
    @Transactional
    public String update(Principal principal, HttpServletRequest request){
        String userName = principal.getName();
        String password = request.getParameter("password");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        sysUserRepository.updatePasswordByUsername(userName,encoder.encode(password));
        return "redirect:/logout";
    }

    @RequestMapping("/getUserList")
    @ResponseBody
    public BootstrapPage getUserList(HttpServletRequest request){
        BootstrapPage page = HttpUtil.searchAll(request,
                Sort.Order.desc("id"), new SysUserQuery(sysUserRepository), new UserPageItemTransfer());
        return page;
    }

    @RequestMapping("/getUserExcelHeads")
    @ResponseBody
    public String getUserExcelHeads(HttpServletRequest request){
        Integer userId = Integer.parseInt(request.getParameter("userId"));
        Optional<SysUser> user = sysUserRepository.findById(Long.valueOf(userId));
        if(user.isPresent()) {
            return ","+user.get().getHeads().stream().map(h->h.getId()+"").collect(Collectors.joining(","))+",";
        }
        return "";
    }

    @RequestMapping("/updateUser")
    @AuthenticRequire(value="ROLE_ADMIN",handle = ModelAndViewReturnHandle.class)
    public ModelAndView updateUser(HttpServletRequest request, RedirectAttributes attr){
        ModelAndView view = new ModelAndView();
        view.setViewName("redirect:/tabUserManagement");

        Integer userId = Integer.parseInt(request.getParameter("update_id"));
        Optional<SysUser> user = sysUserRepository.findById(new Long(userId));
        boolean success = false;
        String name ="";
        if(user.isPresent()) {
            name = user.get().getUsername();
            String selectHeads = request.getParameter("selectHeads");
            if (selectHeads != null && !selectHeads.isEmpty()) {
                String[] heads = selectHeads.split(",");
                user.get().setHeads(
                        Arrays.stream(heads)
                                .map(h -> new ExcelHead(Integer.parseInt(h)))
                                .collect(Collectors.toSet()));
            }else{
                user.get().setHeads(null);
            }
            sysUserRepository.save(user.get());
            success = true;
        }
        if(success)
            attr.addFlashAttribute("notice","修改["+name+"]成功");
        else
            attr.addFlashAttribute("notice","修改["+name+"]失败");

        return view;
    }

    @RequestMapping("/addUser")
    @AuthenticRequire(value = "ROLE_ADMIN",handle = ModelAndViewReturnHandle.class)
    public ModelAndView addUser(HttpServletRequest request, RedirectAttributes attr){
        ModelAndView view = new ModelAndView();
        view.setViewName("redirect:/tabUserManagement");

        String username = request.getParameter("add_username");
        String password = request.getParameter("add_password");
        SysUser user = sysUserRepository.findByUsername(username);
        if(user!=null){ //重名用户存在
            attr.addFlashAttribute("notice","用户名被占用");
            return view;
        }
        SysUser sysUser = new SysUser();
        sysUser.setUsername(username);
        sysUser.setPassword(new BCryptPasswordEncoder().encode(password));
        String selectHeads = request.getParameter("add_selectHeads");
        if (selectHeads != null && !selectHeads.isEmpty()) {
            String[] heads = selectHeads.split(",");
            sysUser.setHeads(
                    Arrays.stream(heads)
                            .map(h -> new ExcelHead(Integer.parseInt(h)))
                            .collect(Collectors.toSet()));
        }else{
            sysUser.setHeads(null);
        }
        List<SysRole> roles = new ArrayList();
        roles.add(new SysRole(2L));
        roles.add(new SysRole(3L));
        sysUser.setRoles(roles);
        sysUserRepository.save(sysUser);
        user = sysUserRepository.findByUsername(username);
        if(user!=null)
            attr.addFlashAttribute("notice","新增["+user.getUsername()+"]成功");
        return view;
    }

    @RequestMapping("delUser")
    public ModelAndView delUser(HttpServletRequest request, RedirectAttributes attr){
        ModelAndView view = new ModelAndView();
        view.setViewName("redirect:/tabUserManagement");
        Long userId = Long.parseLong(request.getParameter("id"));
        if(userId.equals(1)) {
            attr.addFlashAttribute("notice","不允许删除管理员账号");
            return view;
        }
        Optional<SysUser> user = sysUserRepository.findById(userId);
        String name = "";
        boolean succ = false;
        if(user.isPresent()) {
            name = user.get().getUsername();
            sysUserRepository.deleteById(userId);
            user = sysUserRepository.findById(userId);
            if(!user.isPresent())
                succ = true;
        }
        if(succ)
            attr.addFlashAttribute("notice","删除["+name+"]成功");
        else
            attr.addFlashAttribute("notice","删除["+name+"]失败");
        return view;
    }

}

class UserPageItemTransfer implements IPageItemTransfer<SysUser>{
    @Override
    public BootstrapPageItem getBootstrapPageItem(Pageable pageable, SysUser item) {
        SysUserItem sysUserItem = new SysUserItem();
        sysUserItem.setId(item.getId());
        sysUserItem.setUsername(item.getUsername());

        sysUserItem.setPage(pageable.getPageNumber());
        sysUserItem.setPageSize(pageable.getPageSize());
        sysUserItem.setOffset((int) pageable.getOffset());
        return sysUserItem;
    }
}

