package demo.controller;

import demo.service.validatecode.ValidateCode;
import demo.service.validatecode.ValidateCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 验证码请求，重新生成一个新的验证码
 */
@RestController
public class ValidateCodeController {
    @Autowired
    private ValidateCodeService validateCodeCreateService;

    @GetMapping("/get-validate-code")
    public void getImageCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 创建验证码
        ValidateCode validateCode = validateCodeCreateService.createImageCode();
        // 将验证码放到session中（推荐放在Redis中，可设置过期时间能自动删除）
        request.getSession().setAttribute("validate-code", validateCode);
        // 返回验证码给前端
        ImageIO.write(validateCode.getImage(), "JPEG", response.getOutputStream());
    }
}
