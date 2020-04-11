package demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 提供路径操作
 * 分别用来获取三个路径：最新目录、备份目录、临时目录
 * 最新目录底下存放当前最新版本的文件
 */
@Service
public class PathService {
    private final String LASTEST_DIR = "lastest";
    private final String BACKUP_DIR = "backup";
    private final String TEMP_DIR = "temp";
    @Value("${app.file.root.dir}")
    private String root;

    public String getLastestPath(IdGenEnum idGenEnum){
        return root+"/"+ idGenEnum.getName()+"/"+LASTEST_DIR;
    }

    public String getBackupPath(IdGenEnum idGenEnum){
        return root+"/"+ idGenEnum.getName()+"/"+BACKUP_DIR;
    }

    public String getTempPath(IdGenEnum idGenEnum){
        return root+"/"+ idGenEnum.getName()+"/"+TEMP_DIR;
    }

}
