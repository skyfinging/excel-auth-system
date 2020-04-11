package demo.dao;

import demo.dao.bean.LastFileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LastFileInfoRepository extends JpaRepository<LastFileInfo, Long> {

    @Query("from LastFileInfo where version=?1")
    LastFileInfo findByVersion(long version);
}
