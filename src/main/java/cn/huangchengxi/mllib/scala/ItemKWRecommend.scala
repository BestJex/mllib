package cn.huangchengxi.mllib.scala

import java.{lang, util}

import cn.huangchengxi.mllib.beans.RecommendItem
import cn.huangchengxi.mllib.conf.ItemRecommend

class ItemKWRecommend extends ItemRecommend{
  override def getRecommended(uid: lang.Long, cnt: Int): util.List[RecommendItem] = ???
}
