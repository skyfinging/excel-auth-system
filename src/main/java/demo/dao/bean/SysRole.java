package demo.dao.bean;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class SysRole {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    public SysRole(){}

    public SysRole(Long id){
        this.id = id;
    }

}
