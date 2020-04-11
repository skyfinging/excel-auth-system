package demo.view.filter;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class VcAuthenticationFailureHandler implements AuthenticationFailureHandler {


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if(exception instanceof ValidateCodeException){
            String strUrl = request.getContextPath() + "/login?error";
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION,exception);
            response.sendRedirect(strUrl);
        }
    }
}
