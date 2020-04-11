package demo.dao;

import demo.dao.bean.ColumnRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColumnRoleRepository extends JpaRepository<ColumnRole, Long> {

    ColumnRole findColumnRoleByColumnName(String columnName);
}
