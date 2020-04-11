package demo.controller;

import demo.dao.ExcelHeadRepository;
import demo.dao.bean.ExcelHead;
import demo.service.IdGenEnum;
import demo.service.IdGenServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.List;

/**
 * 登录流程
 *      /login  ->  /   ->  /home
 *     "/"自动跳转到"/home",解决了登录之后，浏览器url还是/login,假如这时候刷新页面，会重新使用POST方式请求/login,导致报错
 *     同时，登录成功之后的"/"请求，无法获取到登录信息，经过跳转之后，才能正常获取登录信息Principal
 */
@Controller
public class HomeController {

    @Autowired
    private IdGenServices idGenServices;

    @Autowired
    private ExcelHeadRepository excelHeadRepository;

    @RequestMapping("/")
    public String index(){
        return "index";
    }

    @RequestMapping("/home")
    public String home(HttpServletRequest request,Model model){
        model.addAttribute("menu_active", "home");
        long id = idGenServices.getCurrentId(IdGenEnum.File);
        request.getSession().setAttribute(IdGenEnum.File+"_version",id);
        return "home-tab-fileUpload";
    }

    @RequestMapping("/tabUserInfo")
    public String tabUserInfo(HttpServletRequest request,Model model, Principal principal){
        if(principal!=null) {
            model.addAttribute("userName", principal.getName());
            model.addAttribute("menu_active", "tabUserInfo");
        }
        return "home-tab-userInfo";
    }

    @RequestMapping("/tabUserManagement")
    public String tabUserManagement(HttpServletRequest request,Model model){
        model.addAttribute("menu_active", "tabUserManagement");
        List<ExcelHead> list = excelHeadRepository.findAll();
        model.addAttribute("heads", list);
        return "home-tab-userManagement";
    }

    @RequestMapping("/tabTableHead")
    public String tabTableHead(HttpServletRequest request, Model model){
        model.addAttribute("menu_active", "tabTableHead");
        return "home-tab-tableHead";
    }
}
