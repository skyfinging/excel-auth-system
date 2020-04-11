package demo.config;

import demo.view.filter.ValidateCodeFilter;
import demo.view.filter.VcAuthenticationFailureHandler;
import demo.service.CustomUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserService customUserService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //设置用户密码加密方式
        auth.userDetailsService(customUserService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //验证码过滤器
        ValidateCodeFilter validateCodeFilter = new ValidateCodeFilter();
        validateCodeFilter.setAuthenticationFailureHandler(new VcAuthenticationFailureHandler());
        http.addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                //验证码请求不需要权限
                .antMatchers("/get-validate-code").permitAll()

                .anyRequest().authenticated()
                .and()
                    .formLogin()
                    .loginPage("/login")
                    .failureUrl("/login?error")
                    .successForwardUrl("/")
                    .permitAll()
                .and()
                    .logout()
                    .logoutUrl("/logout")
                    //登出成功之后，自动跳转到登录页面
                    .logoutSuccessUrl("/login?logout")
                    //登出的时候，设置session失效
                    .invalidateHttpSession(true)
                    //登出页面允许使用get方式
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                    .permitAll();

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //解决静态资源被拦截的问题
        //用户没有登录的时候，这时候没有权限，无法获取静态资源，导致登录页面无法熏染css
        web.ignoring().antMatchers("/css/**","js/**","/images/**");
    }
}
