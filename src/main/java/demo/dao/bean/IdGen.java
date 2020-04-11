package demo.dao.bean;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class IdGen {
    @Id
    private String name;
    private long id;

    public IdGen(){}

    public IdGen(String name, long id){
        this.name = name;
        this.id = id;
    }
}
