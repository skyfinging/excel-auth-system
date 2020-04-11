package demo.service;

import lombok.Getter;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Id类型，一个文件对应一个Id类型
 * 目前只管理到一个文件，所以只需要一个枚举
 * 后续如果还有新的文件需要管理版本，再加类型
 */
public enum IdGenEnum {
    File("File");

    @Getter
    private final String name;
    @Getter
    private final ReadWriteLock readWriteLock;

    IdGenEnum(String name){
        this.name = name;
        this.readWriteLock = new ReentrantReadWriteLock();
    }
}
