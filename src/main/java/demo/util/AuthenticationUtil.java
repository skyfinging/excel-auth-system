package demo.util;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

/**
 * 权限认证操作
 */
public class AuthenticationUtil {

    /**
     * 判断用户是否登录
     * @param principal
     * @param result
     * @return
     */
    public static boolean authen(AbstractAuthenticationToken principal, Map<String, Object> result){
        if(principal==null) {
            result.put("error", "用户未登陆");
            return false;
        }
        return true;
    }

    /**
     * 判断用户是否具备某个权限
     * @param principal
     * @param roleName
     * @param result
     * @return
     */
    public static boolean authenRole(AbstractAuthenticationToken principal, String roleName, Map<String, Object> result){
        if(roleName==null || roleName.isEmpty())
            return true;
        boolean isRole = principal.getAuthorities().stream()
                .map(o->o.getAuthority()).filter(o->o.equalsIgnoreCase(roleName)).count()>0;
        if(!isRole){
            if(result!=null)
                result.put("error", "当前用户没有操作权限");
            return false;
        }
        return true;
    }
}
