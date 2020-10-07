package cn.huangchengxi.mllib.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class UserEntity {
    @Id
    @GeneratedValue
    public Long uid;
    public String uname;

    public UserEntity(){

    }
    public UserEntity(Long uid,String name){
        this.uid=uid;
        this.uname=name;
    }
    public UserEntity(String name){
        this.uname=name;
    }
}
