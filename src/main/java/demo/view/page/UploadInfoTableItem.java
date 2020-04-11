package demo.view.page;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class UploadInfoTableItem extends BootstrapPageItem {
    private long id;
    private String fileName;
    private String userName;
    private long fileSize;
    private Timestamp uploadTime;
    private long version;
}
