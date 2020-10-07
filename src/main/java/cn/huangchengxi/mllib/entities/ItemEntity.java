package cn.huangchengxi.mllib.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ItemEntity {
    @Id
    @GeneratedValue
    public Long iid;
    public String iname;

    public ItemEntity(){

    }
    public ItemEntity(Long id,String name){
        this.iid=id;
        this.iname=name;
    }
    public ItemEntity(String name){
        this.iname=name;
    }
}
