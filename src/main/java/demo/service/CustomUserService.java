package demo.service;

import demo.dao.SysUserRepository;
import demo.dao.bean.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.annotation.Resources;

/**
 * spring security使用
 */
@Service
@PropertySource("messages.properties")
public class CustomUserService implements UserDetailsService {
    protected MessageSourceAccessor messages;

    @Autowired
    SysUserRepository userRepository;

    public CustomUserService(MessageSource messageSource){
        messages = new MessageSourceAccessor(messageSource);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = userRepository.findByUsername(username);
        if(sysUser==null)
            throw new MyLoginException(messages.getMessage("UserDetailsAuthenticationProvider.userNotFound","用户不存在"));
        return sysUser;
    }
}

class MyLoginException extends AuthenticationException{

    public MyLoginException(String message){
        super(message);
    }
}
