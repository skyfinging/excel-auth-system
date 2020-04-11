package demo.view.filter;

import demo.service.validatecode.ValidateCode;
import lombok.Setter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.thymeleaf.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ValidateCodeFilter extends OncePerRequestFilter {
    // spring的正则匹配
    private static final PathMatcher pathMatcher = new AntPathMatcher();

    @Setter
    protected AuthenticationFailureHandler authenticationFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 只有登录请求‘/login’,并且为'post'请求时，才校验
        if ("POST".equals(request.getMethod())
                && pathMatcher.match("/login", request.getServletPath())) {
            try {
                codeValidate(request);
            } catch (ValidateCodeException e) {
                // 验证码不通过，跳到错误处理器处理
                authenticationFailureHandler.onAuthenticationFailure(request, response, e);
                return;
            }
        }
        doFilter(request, response, filterChain);
    }

    private void codeValidate(HttpServletRequest request) {
        // 获取到传入的验证码
        String codeInRequest = request.getParameter("validateCode");
        ValidateCode codeInSession = (ValidateCode) request.getSession(false).getAttribute("validate-code");

        // 校验验证码是否正确
        if (StringUtils.isEmpty(codeInRequest)) {
            throw new ValidateCodeException("验证码的值不能为空");
        }
        if (codeInSession == null) {
            throw new ValidateCodeException("验证码不存在");
        }
        if (codeInSession.isExpired()) {
            throw new ValidateCodeException("验证码已过期");
        }
        if (!StringUtils.equals(codeInSession.getCode(), codeInRequest)) {
            throw new ValidateCodeException("验证码不匹配");
        }

        // 校验正确后，移除session中验证码
        request.getSession(false).removeAttribute("validate-code");
    }
}

class ValidateCodeException extends AuthenticationException {
    public ValidateCodeException(String message) {
        super(message);
    }
}
