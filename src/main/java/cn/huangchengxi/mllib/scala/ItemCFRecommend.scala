package cn.huangchengxi.mllib.scala

import java.{lang, util}

import cn.huangchengxi.mllib.beans.{ItemSemi, RecommendItem}
import cn.huangchengxi.mllib.conf.ItemRecommend
import cn.huangchengxi.mllib.entities.ItemPref
import org.apache.spark.rdd.RDD
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import scala.collection.mutable
import scala.collection.JavaConverters._

@Component
class ItemCFRecommend @Autowired()(val userRDD: UserRDD, val occurrenceSim: OccurrenceSim) extends ItemRecommend{
  def Recommend(items_similar: RDD[ItemSemi], user_prefer: RDD[ItemPref], r_number: Int): (RDD[RecommendItem]) = {
    //1.数据准备
    val rdd_app1_R1: RDD[(Long, Long, Double)] = items_similar.map(f => (f.iid1, f.iid2, f.similarity))
    val user_prefer1: RDD[(Long, Long, Double)] = user_prefer.map(f => (f.uid, f.itemId, f.starPref))
    //2.矩阵计算（i行j列join）
    val rdd_app1_R2: RDD[(Long, ((Long, Double), (Long, Double)))] = rdd_app1_R1.map(f => (f._1, (f._2, f._3))).join(user_prefer1.map(f => (f._2, (f._1, f._3))))
    //3.矩阵计算（i行j列相乘）
    val rdd_app1_R3: RDD[((Long, Long), Double)] = rdd_app1_R2.map(f => ((f._2._2._1, f._2._1._1), f._2._2._2 * f._2._1._2))
    //4.矩阵计算（用户：元素累加求和）
    val rdd_app1_R4: RDD[((Long, Long), Double)] = rdd_app1_R3.reduceByKey((x, y) => x + y)
    //5.矩阵计算（用户：对结果过滤已有物品）
    val rdd_app1_R5: RDD[(Long, (Long, Double))] = rdd_app1_R4.leftOuterJoin(user_prefer1.map(f => ((f._1, f._2), 1))).filter(f => f._2._2.isEmpty).map(f => (f._1._1, (f._1._2, f._2._1)))
    //6.矩阵计算（用户：用户对结果排序，过滤）
    val rdd_app1_R6: RDD[(Long, Iterable[(Long, Double)])] = rdd_app1_R5.groupByKey()
    val rdd_app1_R7: RDD[(Long, Iterable[(Long, Double)])] = rdd_app1_R6.map(f => {
      val i2: mutable.Buffer[(Long, Double)] = f._2.toBuffer
      val i2_2: mutable.Buffer[(Long, Double)] = i2.sortBy(_._2)
      if (i2_2.length > r_number) i2_2.remove(0, (i2_2.length - r_number))
      (f._1, i2_2.toIterable)
    })
    val rdd_app1_R8: RDD[(Long, Long, Double)] = rdd_app1_R7.flatMap(f => {
      val id2: Iterable[(Long, Double)] = f._2
      for (w <- id2) yield (f._1, w._1, w._2)
    })
    rdd_app1_R8.map(f => new RecommendItem(f._1, f._2, f._3))
  }
  def Recommend(items_similar: RDD[ItemSemi], user_prefer: RDD[ItemPref]): (RDD[RecommendItem]) = {
    //1.数据准备
    val rdd_app1_R1: RDD[(Long, Long, Double)] = items_similar.map(f => (f.iid1, f.iid2, f.similarity))
    val user_prefer1: RDD[(Long, Long, Double)] = user_prefer.map(f => (f.uid, f.itemId, f.starPref))
    //2.矩阵计算（i行和j列join）
    val rdd_app1_R2: RDD[(Long, ((Long, Double), (Long, Double)))] = rdd_app1_R1.map(f => (f._1, (f._2, f._3))).join(user_prefer1.map(f => (f._2, (f._1, f._3))))
    //3.矩阵计算（i行j列元素相乘）
    val rdd_app1_R3: RDD[((Long, Long), Double)] = rdd_app1_R2.map(f => ((f._2._2._1, f._2._1._1), f._2._2._2 * f._2._1._2))
    //4.矩阵计算（用户：元素累加求和）
    val rdd_app1_R4: RDD[((Long, Long), Double)] = rdd_app1_R3.reduceByKey((x, y) => x + y)
    //5.矩阵计算（用户：对结果过滤已有物品）
    val rdd_app1_R5: RDD[(Long, (Long, Double))] = rdd_app1_R4.leftOuterJoin(user_prefer1.map(f => ((f._1, f._2), 1))).filter(f => f._2._2.isEmpty).map(f => (f._1._1, (f._1._2, f._2._1)))
    //6.矩阵计算（用户：用户对结果排序，过滤）
    val rdd_app1_R6: RDD[(Long, Long, Double)] = rdd_app1_R5.map(f => (f._1, f._2._1, f._2._2)).sortBy(f => (f._1, f._3))
    rdd_app1_R6.map(f => new RecommendItem(f._1, f._2, f._3))
  }

  override def getRecommended(uid: lang.Long, cnt: Int): util.List[RecommendItem] = {
    val r=Recommend(occurrenceSim.OccurrenceSimilarity(),userRDD.UserRDD(uid))
    r.collect().toList.asJava
  }
}
