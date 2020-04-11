package demo.dao;

import demo.dao.bean.IdGen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


public interface IdGenrepository extends JpaRepository<IdGen, Long> {
    IdGen findByName(String name);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "update IdGen set id=?2 where name=?1")
    void updateIdGen(String name, Long id);
}
