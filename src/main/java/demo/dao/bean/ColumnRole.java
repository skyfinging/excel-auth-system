package demo.dao.bean;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class ColumnRole {
    @Id
    @GeneratedValue
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUST_SEQ_COLUMN_ROLE")
//    @SequenceGenerator(sequenceName = "customer_seq_columnRole", allocationSize = 1, name = "CUST_SEQ_COLUMN_ROLE")
    private long id;

    private String columnName;
    private String roleName;

}
