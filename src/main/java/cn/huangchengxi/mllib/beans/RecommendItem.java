package cn.huangchengxi.mllib.beans;

import java.io.Serializable;

public class RecommendItem implements Serializable {
    public Long uid;
    public Long iid;
    public Double pref;

    public RecommendItem(Long uid, Long iid, Double pref){
        this.iid=iid;
        this.uid=uid;
        this.pref=pref;
    }

    @Override
    public String toString() {
        return "RecommendItem{" +
                "uid=" + uid +
                ", iid=" + iid +
                ", pref=" + pref +
                '}';
    }
}
