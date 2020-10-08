package cn.huangchengxi.mllib.scala

import java.util

import cn.huangchengxi.mllib.beans.ItemSemi
import cn.huangchengxi.mllib.conf.ItemSimilarity
import cn.huangchengxi.mllib.entities.ItemPref
import cn.huangchengxi.mllib.repos.ItemPrefRepository
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.rdd.RDD
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import scala.collection.JavaConverters._
import scala.math._

@Component
class EuclideanSim @Autowired()(val itemPrefRepository:ItemPrefRepository,
                                val jsc:JavaSparkContext) extends ItemSimilarity{
  def EuclideanDistanceSimilarity(): (RDD[ItemSemi]) = {
    val list=itemPrefRepository.findAll()
    val user_rdd:RDD[ItemPref]=jsc.parallelize(list)
    //1.数据准备
    val user_rdd1: RDD[(Long, Long, Double)] = user_rdd.map(f => (f.uid, f.itemId, f.starPref))
    val user_rdd2: RDD[(Long, (Long, Double))] = user_rdd1.map(f => (f._1, (f._2, f._3)))
    //2.(用户，物品，评分)笛卡尔积操作=>（物品1，物品2，评分1，评分2）组合
    val user_rdd3: RDD[(Long, ((Long, Double), (Long, Double)))] = user_rdd2 join user_rdd2
    val user_rdd4: RDD[((Long, Long), (Double, Double))] = user_rdd3.map(f => ((f._2._1._1, f._2._2._1), (f._2._1._2, f._2._2._2)))
    //3.(物品1，物品2，评分1，评分2)组合=>（物品1，物品2，评分1-评分2）组合并累加
    val user_rdd5: RDD[((Long, Long), Double)] = user_rdd4.map(f => (f._1, (f._2._1 - f._2._2) * (f._2._1 - f._2._2))).reduceByKey(_ + _)
    //4.(物品1，物品2，评分1，评分2)组合=>（物品1，物品2,1）组合计算物品1和物品2的重叠度
    val user_rdd6: RDD[((Long, Long), Int)] = user_rdd4.map(f => (f._1, 1)).reduceByKey(_ + _)
    //5.非对角矩阵
    val user_rdd7: RDD[((Long, Long), Double)] = user_rdd5.filter(f => f._1._1 != f._1._2)
    //6.相似度计算
    val user_rdd8: RDD[((Long, Long), (Double, Int))] = user_rdd7.join(user_rdd6)
    val user_rdd9: RDD[(Long, Long, Double)] = user_rdd8.map(f => (f._1._1, f._1._2, f._2._2 / (1 + sqrt(f._2._1))))
    //7.结果返回
    user_rdd9.map(f => new ItemSemi(f._1, f._2, f._3))
  }

  override def calculateResult(): util.List[ItemSemi] = EuclideanDistanceSimilarity().collect().toList.asJava
}
