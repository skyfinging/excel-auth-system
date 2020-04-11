package demo.service.query;

import demo.dao.ExcelHeadRepository;
import demo.dao.bean.ExcelHead;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Path;

public class ExcelHeadQuery implements IQuery<ExcelHead> {
    private ExcelHeadRepository jpaRepository ;

    public ExcelHeadQuery(ExcelHeadRepository jpaRepository){
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Page<ExcelHead> findAll(Pageable pageable, JSONObject jsonObject) {
        String searchName = jsonObject.getString("search_name");
        Specification<ExcelHead> confusion= (Specification<ExcelHead>) (root, query, criteriaBuilder) -> {
            Path<String> name = root.get("name");
            return criteriaBuilder.like(name, "%"+searchName+"%");
        };
        return jpaRepository.findAll(confusion, pageable);
    }
}
