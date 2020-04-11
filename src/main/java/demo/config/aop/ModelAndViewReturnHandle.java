package demo.config.aop;

import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * 页面请求，返回登陆页面
 */
public class ModelAndViewReturnHandle implements IReturnHandle<Map<String, Object>, ModelAndView> {

    @Override
    public ModelAndView handle(Map<String, Object> stringStringMap) {
        ModelAndView view = new ModelAndView();
        view.setViewName("redirect:/logout");
        return view;
    }
}
