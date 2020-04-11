package demo.service;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * 提供文件操作
 */
@Service
@Log4j2
public class FileService {

    @Autowired
    private PathService pathService;

    /**
     * 把版本号加到文件名后面
     * @param fileName
     * @param version
     * @return
     */
    public String getBackUpFileName(String fileName, long version){
        if(fileName==null)
            return null;
        int index = fileName.lastIndexOf(".");
        if(index>-1){
            return fileName.substring(0,index)+"_"+version+fileName.substring(index);
        }
        return fileName;
    }

    /**
     * 准备文件路径，如果文件路径不存在，则创建文件夹
     * @param idGenEnum
     */
    public void preparedDir(IdGenEnum idGenEnum){
        File backupDir = new File(pathService.getBackupPath(idGenEnum));
        backupDir.mkdirs();
        File lastestDir = new File(pathService.getLastestPath(idGenEnum));
        lastestDir.mkdirs();
        File tempDir = new File(pathService.getTempPath(idGenEnum));
        tempDir.mkdirs();
    }

    /**
     * 把最新文件夹底下的文件都清掉
     * 清除失败的忽略掉
     * @param idGenEnum
     */
    public void clearLastestFile(IdGenEnum idGenEnum) throws IOException {
        String path = pathService.getLastestPath(idGenEnum);
        File dir = new File(path);
        FileUtils.forceDelete(dir);
    }

    /**
     * 获取备份文件
     * @param idGenEnum
     * @param fileName
     * @return
     */
    public File getBackupFile(IdGenEnum idGenEnum, String fileName){
        String path = pathService.getBackupPath(idGenEnum);
        return new File(path+"/"+fileName);
    }

    /**
     * 获取最新文件的文件目录
     * @param idGenEnum
     * @return
     */
    public File getLastestDir(IdGenEnum idGenEnum){
        String path = pathService.getLastestPath(idGenEnum);
        return new File(path);
    }

    /**
     * 获取最新文件
     * @param idGenEnum
     * @param fileName
     * @return
     */
    public File getLastestFile(IdGenEnum idGenEnum, String fileName){
        String path = pathService.getLastestPath(idGenEnum);
        return new File(path+"/"+fileName);
    }

    /**
     * 将文件备份到备份目录中
     * @param idGenEnum
     * @param fileName
     * @param fileData
     */
    public File backupFile(IdGenEnum idGenEnum, String fileName, byte[] fileData) {
        String path = pathService.getBackupPath(idGenEnum);
        try {
            return saveFile(path, fileName, fileData);
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 把最新文件备份到备份目录中
     * @param idGenEnum
     * @param version
     */
    public void backupLastestFile(IdGenEnum idGenEnum, long version){
        String lastesFile = pathService.getLastestPath(idGenEnum)+"/"+version;
        String backupDir = pathService.getBackupPath(idGenEnum);
        copyFileToDir(lastesFile, backupDir);
    }

    /**
     * 把文件内容写入到临时目录，并返回该临时文件
     * @param idGenEnum
     * @param fileName
     * @param fileData
     * @return
     */
    public File saveTempFile(IdGenEnum idGenEnum, String fileName, byte[] fileData){
        String path = pathService.getTempPath(idGenEnum);
        try {
            return saveFile(path, fileName, fileData);
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 保存文件到指定目录
     * @param filePath
     * @param fileName
     * @param bytes
     * @return
     * @throws IOException
     */
    private File saveFile(String filePath, String fileName, byte[] bytes) throws IOException {
        File file = new File(filePath+"/"+fileName);
        try {
            FileUtils.writeByteArrayToFile(file, bytes);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        return file;
    }

    /**
     * 拷贝文件到指定目录
     * @param srcFileName
     * @param dstDir
     */
    private void copyFileToDir(String srcFileName, String dstDir){
        try {
            FileUtils.copyFileToDirectory(new File(srcFileName),new File(dstDir));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            log.warn("备份文件失败:"+srcFileName);
        }
    }
}
