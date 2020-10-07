package cn.huangchengxi.mllib.beans;

public class UserItemPair {
    Long uid;
    Long iid;

    public UserItemPair(Long uid,Long iid){
        this.iid=iid;
        this.uid=uid;
    }
}
