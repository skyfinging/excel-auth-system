package demo.dao.bean;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
public class LastFileInfo {
    @Id
    @GeneratedValue
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUST_SEQ")
//    @SequenceGenerator(sequenceName = "customer_seq", allocationSize = 1, name = "CUST_SEQ")
    private long id;
    private String fileName;
    private String userName;
    private long fileSize;
    private Timestamp uploadTime;
    private long version;

    public LastFileInfo(){}

    public LastFileInfo(String fileName, String userName, long fileSize, Timestamp uploadTime, long version){
        this.fileName = fileName;
        this.userName = userName;
        this.fileSize = fileSize;
        this.uploadTime = uploadTime;
        this.version = version;
    }

}
