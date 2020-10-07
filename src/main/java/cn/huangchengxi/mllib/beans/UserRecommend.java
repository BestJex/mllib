package cn.huangchengxi.mllib.beans;

import java.io.Serializable;

public class UserRecommend implements Serializable {
    public Long uid;
    public Long iid;
    public Double pref;

    public UserRecommend(Long uid,Long iid,Double pref){
        this.iid=iid;
        this.uid=uid;
        this.pref=pref;
    }
}
