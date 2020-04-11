package demo.config.aop;

import java.lang.annotation.*;

/**
 * 用于后台权限控制，在requestMapping声明的方法上一起使用，用来过滤没有权限的请求
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthenticRequire {
    //权限的名称
    String value() default "";

    //没有权限的时候，通过这个接口得到返回值
    Class<? extends IReturnHandle> handle() default DefaultReturnHandle.class;
}
