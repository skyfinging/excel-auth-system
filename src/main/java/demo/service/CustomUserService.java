package demo.service;

import demo.dao.SysUserRepository;
import demo.dao.bean.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.AuthenticationException;
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
    @Value("${UserDetailsAuthenticationProvider.userNotFound}")
    private String userNotFoundMessage;

    @Autowired
    SysUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = userRepository.findByUsername(username);
        if(sysUser==null)
            throw new MyLoginException(userNotFoundMessage);
        return sysUser;
    }
}

class MyLoginException extends AuthenticationException{

    public MyLoginException(String message){
        super(message);
    }
}
