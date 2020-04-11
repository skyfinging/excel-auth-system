package demo.config.aop;

import java.util.Map;

public class DefaultReturnHandle implements IReturnHandle<Map<String, Object>, Map<String, Object>> {

    @Override
    public Map<String, Object> handle(Map<String, Object> stringStringMap) {
        return stringStringMap;
    }
}
