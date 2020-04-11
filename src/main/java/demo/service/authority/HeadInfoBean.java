package demo.service.authority;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 表头信息，记录每一列对应的表头名称
 */
public class HeadInfoBean {
    private Map<Integer, String> headMap;

    public HeadInfoBean(Map<Integer, String> headMap){
        this.headMap = new LinkedHashMap<>();
        if(headMap!=null)
            this.headMap.putAll(headMap);
    }

    public String getColumnNameAt(Integer index){
        return headMap.get(index);
    }

    public Iterator<Integer> iterator(){
        return headMap.keySet().iterator();
    }

    public boolean isEmpty(){
        return headMap.isEmpty();
    }

    public int size(){
        return headMap.size();
    }
}
