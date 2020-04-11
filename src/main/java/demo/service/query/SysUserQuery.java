package demo.service.query;

import demo.dao.SysUserRepository;
import demo.dao.bean.SysUser;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

public class SysUserQuery implements IQuery<SysUser> {
    private SysUserRepository jpaRepository ;

    public SysUserQuery(SysUserRepository jpaRepository){
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Page<SysUser> findAll(Pageable pageable, JSONObject jsonObject) {
        String username = jsonObject.getString("search_username");
        Specification<SysUser> confusion= (Specification<SysUser>) (root, query, criteriaBuilder) -> {
            Path<String> name = root.get("username");
            return criteriaBuilder.like(name, "%"+username+"%");
        };
        return jpaRepository.findAll(confusion, pageable);
    }
}
