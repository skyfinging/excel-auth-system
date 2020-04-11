package demo.service;

import demo.dao.IdGenrepository;
import demo.dao.bean.IdGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 提供id获取操作
 * 从数据库中获取id，更新id
 * 使用数据库主键自增来更新id
 */
@Service
public class IdGenServices {
    @Autowired
    private IdGenrepository idGenrepository;

    /**
     * 获取当前id
     * @param idGenEnum
     * @return
     */
    public long getCurrentId(IdGenEnum idGenEnum){
        synchronized (idGenEnum) {
            IdGen idGen = idGenrepository.findByName(idGenEnum.getName());
            while (idGen==null) {
                idGenrepository.save(new IdGen(idGenEnum.getName(), 0L));
                idGenrepository.flush();
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                }
                idGen = idGenrepository.findByName(idGenEnum.getName());
            }
            long id = idGen.getId();
            return id;
        }
    }

    /**
     * 获取下一个id，并更新当前id为下一个id
     * @param idGenEnum
     * @return
     */
    @Transactional
    public long getNextId(IdGenEnum idGenEnum){
        synchronized (idGenEnum) {
            long id = getCurrentId(idGenEnum);
            idGenrepository.updateIdGen(idGenEnum.getName(), id+1);
            id = getCurrentId(idGenEnum);
            return id;
        }
    }
}
