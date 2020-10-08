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
class OccurrenceSim @Autowired()(val itemPrefRepository:ItemPrefRepository,
                                 val jsc:JavaSparkContext) extends ItemSimilarity{

  def OccurrenceSimilarity(): (RDD[ItemSemi]) = {
    val list=itemPrefRepository.findAll()
    print(list)
    val user_rdd:RDD[ItemPref]=jsc.parallelize(list)
    //1.数据准备
    val user_rdd1: RDD[(Long, Long, Double)] = user_rdd.map(f => (f.uid, f.itemId, f.starPref))
    val user_rdd2: RDD[(Long,Long)] = user_rdd1.map(f => (f._1, f._2))
    //2.(用户，物品)笛卡尔积操作=>物品与物品组合
    val user_rdd3: RDD[(Long, (Long,Long))] = user_rdd2.join(user_rdd2)
    val user_rdd4: RDD[((Long,Long), Int)] = user_rdd3.map(f => (f._2, 1))
    //3.(物品，物品，频次)
    val user_rdd5: RDD[((Long,Long), Int)] = user_rdd4.reduceByKey((a,b)=>a+b)
    //4.对角矩阵
    val user_rdd6: RDD[((Long,Long), Int)] = user_rdd5.filter(f => f._1._1 == f._1._2)
    //5.非对角矩阵
    val user_rdd7: RDD[((Long,Long), Int)] = user_rdd5.filter(f => f._1._1 != f._1._2)
    //6.计算同现相似度(物品1，物品2，同现频次)
    val user_rdd8: RDD[(Long, ((Long,Long, Int), Int))] = user_rdd7.
      map(f => (f._1._1, (f._1._1, f._1._2, f._2))).join(user_rdd6.map(f => (f._1._1, f._2)))
    val user_rdd9: RDD[(Long, (Long,Long, Int, Int))] = user_rdd8.map(f => (f._2._1._2, (f._2._1._1, f._2._1._2, f._2._1._3, f._2._2)))
    val user_rdd10: RDD[(Long, ((Long,Long, Int, Int), Int))] = user_rdd9.join(user_rdd6.map(f => (f._1._1, f._2)))
    val user_rdd11: RDD[(Long, Long, Int, Int, Int)] = user_rdd10.map(f => (f._2._1._1, f._2._1._2, f._2._1._3, f._2._1._4, f._2._2))
    val user_rdd12: RDD[(Long,Long, Double)] = user_rdd11.map(f => (f._1, f._2, (f._3 / sqrt(f._4 * f._5))))
    //7.结果返回
    user_rdd12.map(f => new ItemSemi(f._1,f._2,f._3))
  }

  override def calculateResult(): util.List[ItemSemi] = OccurrenceSimilarity().collect().toList.asJava
}
