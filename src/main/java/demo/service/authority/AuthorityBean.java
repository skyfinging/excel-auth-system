package demo.service.authority;

import demo.dao.bean.ExcelHead;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AuthorityBean {
    //获取所有列对应的角色信息，key：表头名称，value：角色名称
    private Map<String, ExcelHead> cache;

    public AuthorityBean(Set<ExcelHead> heads){
        this.cache = new HashMap<>();
        if(heads!=null)
            heads.forEach(head->cache.put(head.getName(),head));
    }

    public boolean isAuthenInColumnName(String columnName){
        return cache.containsKey(columnName);
    }

}
