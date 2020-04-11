package demo.service.query;

import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 分页查询
 * @param <T>
 */
public interface IQuery<T>  {
    Page<T> findAll(Pageable pageable, JSONObject jsonObject);
}
