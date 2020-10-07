package cn.huangchengxi.mllib.beans;

import java.io.Serializable;

public class ItemSemi implements Serializable {
    public Long iid1;
    public Long iid2;
    public Double similarity;
    public ItemSemi(Long iid1,Long iid2,Double sim){
        this.iid1=iid1;
        this.iid2=iid2;
        this.similarity=sim;
    }

    @Override
    public String toString() {
        return "ItemSemi{" +
                "iid1=" + iid1 +
                ", iid2=" + iid2 +
                ", similarity=" + similarity +
                '}';
    }
}
