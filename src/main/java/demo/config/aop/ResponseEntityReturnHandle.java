package demo.config.aop;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * rest请求返回空内容
 */
public class ResponseEntityReturnHandle implements IReturnHandle<Map<String, Object>, ResponseEntity<InputStreamResource>> {

    @Override
    public ResponseEntity<InputStreamResource> handle(Map<String, Object> stringStringMap) {
        return ResponseEntity.ok().contentLength(0).body(null);
    }
}
