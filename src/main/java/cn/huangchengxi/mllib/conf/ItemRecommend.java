package cn.huangchengxi.mllib.conf;

import cn.huangchengxi.mllib.beans.RecommendItem;

import java.util.List;

public interface ItemRecommend {
    List<RecommendItem> getRecommended(Long uid,int cnt);
}
