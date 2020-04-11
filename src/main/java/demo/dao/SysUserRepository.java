package demo.dao;

import demo.dao.bean.SysUser;
import demo.service.query.IQuery;
import org.hibernate.annotations.DynamicUpdate;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SysUserRepository extends JpaRepository<SysUser,Long>, JpaSpecificationExecutor<SysUser> {
    SysUser findByUsername(String username);

    @Modifying
    @Query("update SysUser set password=?2 where username=?1")
    void updatePasswordByUsername(String username, String password);

}
