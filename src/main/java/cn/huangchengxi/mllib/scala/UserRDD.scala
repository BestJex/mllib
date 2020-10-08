package cn.huangchengxi.mllib.scala

import cn.huangchengxi.mllib.entities.ItemPref
import cn.huangchengxi.mllib.repos.ItemPrefRepository
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.rdd.RDD
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserRDD @Autowired()(val jsc:JavaSparkContext,val repo:ItemPrefRepository){
  def UserRDD(uid:Long): RDD[ItemPref] ={
    val ul=repo.findAllByUid(uid)
    val rdd=jsc.parallelize(ul)
    rdd
  }
}
