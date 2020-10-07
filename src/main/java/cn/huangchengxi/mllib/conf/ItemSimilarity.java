package cn.huangchengxi.mllib.conf;

import cn.huangchengxi.mllib.beans.ItemSemi;

import java.util.List;

public interface ItemSimilarity {
    List<ItemSemi> calculateResult();
}
