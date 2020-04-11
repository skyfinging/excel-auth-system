package demo.service.authority;

import demo.dao.SysUserRepository;
import demo.dao.bean.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 查询用户具有哪些excel表头的权限
 */
@Service
public class AuthorityService {
    @Autowired
    private SysUserRepository sysUserRepository;

    public AuthorityBean getColumnName(String username){
        SysUser user = sysUserRepository.findByUsername(username);
        AuthorityBean authorityBean = null;
        if(user!=null){
            authorityBean = new AuthorityBean(user.getHeads());
        }
        return authorityBean;
    }
}
