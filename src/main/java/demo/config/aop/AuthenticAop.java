package demo.config.aop;

import demo.util.AuthenticationUtil;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 非法用户要请求没有权限的接口内容，使用该AOP进行过滤
 * 比如一个没有下载权限的用户，非法构造了一个下载请求
 */
@Aspect
@Component
@Log4j2
public class AuthenticAop {
    @Pointcut("@annotation(demo.config.aop.AuthenticRequire)")
    public void operationLog(){}

    @Around("operationLog()")
    public Object around(ProceedingJoinPoint pjp){
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        AuthenticRequire authenticRequire = method.getAnnotation(AuthenticRequire.class);

        Map<String, Object> result = new HashMap<>();
        boolean authen = false;
        Object[] args = pjp.getArgs();
        if(args!=null){
            //从方法的参数中，过滤出HttpServletRequest参数
            Optional<Object> objectOptional = Arrays.stream(args).filter(arg->arg instanceof HttpServletRequest).findFirst();
            if(objectOptional.isPresent()) {
                HttpServletRequest request = (HttpServletRequest) objectOptional.get();
                //判断用户有没有登录，如果有登录，再判断有没有权限
                authen = AuthenticationUtil.authen((AbstractAuthenticationToken) request.getUserPrincipal(), result);
                if(authen) {
                    String roleName = authenticRequire.value();
                    authen = AuthenticationUtil.authenRole((AbstractAuthenticationToken) request.getUserPrincipal(), roleName, result);
                }
            }
        }
        //如果没有权限，直接返回
        if(!authen){
            if(authenticRequire.handle()!=null){
                try {
                    IReturnHandle<Map<String, Object>,?> handle = authenticRequire.handle().newInstance();
                    return handle.handle(result);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            return result;
        }

        //有相应的权限，执行方法，并返回结果
        Object methodReturn = null;
        try {
            methodReturn = pjp.proceed();
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
        }
        return methodReturn;
    }
}
