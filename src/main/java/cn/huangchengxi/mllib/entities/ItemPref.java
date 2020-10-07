package cn.huangchengxi.mllib.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity
public class ItemPref implements Serializable {
    @Id
    @GeneratedValue
    public Long ipid;
    public Long itemId;
    public Long uid;
    public Double starPref;
    public Date starTime;
}
