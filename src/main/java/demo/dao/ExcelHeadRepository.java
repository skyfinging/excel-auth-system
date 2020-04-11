package demo.dao;

import demo.dao.bean.ExcelHead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ExcelHeadRepository extends JpaRepository<ExcelHead, Integer>, JpaSpecificationExecutor<ExcelHead> {

    @Query("select max(id) from ExcelHead")
    Integer getMaxId();

    @Modifying
    @Query(value="delete from sys_user_heads where heads_id=?1", nativeQuery = true)
    void deleteAllUserHeadsById(Integer id);
}
