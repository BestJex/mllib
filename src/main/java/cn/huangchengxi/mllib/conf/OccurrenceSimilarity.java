package cn.huangchengxi.mllib.conf;

import cn.huangchengxi.mllib.beans.ItemSemi;
import cn.huangchengxi.mllib.entities.ItemPref;
import cn.huangchengxi.mllib.repos.ItemPrefRepository;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple4;
import scala.Tuple5;

import java.util.List;

@Component
public class OccurrenceSimilarity implements ItemSimilarity {
    @Autowired
    ItemPrefRepository itemPrefRepository;
    @Autowired
    JavaSparkContext jsc;

    @Override
    public List<ItemSemi> calculateResult() {
        List<ItemPref> userRdd=itemPrefRepository.findAll();

        //prepare data
        JavaRDD<ItemPref> userRdd1=jsc.parallelize(userRdd);

        JavaPairRDD<Long,Long> userRdd2=userRdd1.mapToPair((PairFunction<ItemPref, Long, Long>) itemPref -> new Tuple2<>(itemPref.uid,itemPref.itemId));
        //笛卡尔积
        JavaPairRDD<Long,Tuple2<Long,Long>> userRdd3=userRdd2.join(userRdd2);
        JavaPairRDD<Tuple2<Long,Long>,Integer> userRdd4=userRdd3.mapToPair((PairFunction<Tuple2<Long, Tuple2<Long, Long>>, Tuple2<Long, Long>, Integer>) f -> new Tuple2<>(f._2, 1));
        //物品，物品，频率
        JavaPairRDD<Tuple2<Long,Long>,Integer> userRdd5=userRdd4.reduceByKey((Function2<Integer, Integer, Integer>) Integer::sum);
        //对角矩阵
        JavaPairRDD<Tuple2<Long,Long>,Integer> userRdd6=userRdd5.filter((Function<Tuple2<Tuple2<Long, Long>, Integer>, Boolean>) v1 -> v1._1._1.equals(v1._1._2));
        //非对角矩阵
        JavaPairRDD<Tuple2<Long,Long>,Integer> userRdd7=userRdd5.filter((Function<Tuple2<Tuple2<Long, Long>, Integer>, Boolean>) v1 -> !v1._1._1.equals(v1._1._2));
        //计算同现相似度
        JavaPairRDD<Long, Tuple3<Long,Long,Integer>> userRdd81=userRdd7.mapToPair((PairFunction<Tuple2<Tuple2<Long, Long>, Integer>, Long, Tuple3<Long, Long, Integer>>) f -> new Tuple2<>(f._1._1,new Tuple3<>(f._1._1,f._1._2,f._2)));
        JavaPairRDD<Long,Integer> userRdd82=userRdd6.mapToPair((PairFunction<Tuple2<Tuple2<Long, Long>, Integer>, Long, Integer>) f -> new Tuple2<>(f._1._1,f._2));
        JavaPairRDD<Long,Tuple2<Tuple3<Long,Long,Integer>,Integer>> userRdd8=userRdd81.join(userRdd82);

        JavaPairRDD<Long, Tuple4<Long,Long,Integer,Integer>> userRdd9=userRdd8.mapToPair((PairFunction<Tuple2<Long, Tuple2<Tuple3<Long, Long, Integer>, Integer>>, Long, Tuple4<Long, Long, Integer, Integer>>) f -> new Tuple2<>(f._2._1._2(),new Tuple4<>(f._2._1._1(),f._2._1._2(),f._2._1._3(),f._2._2)));
        JavaPairRDD<Long,Tuple2<Tuple4<Long,Long,Integer,Integer>,Integer>> userRdd10=userRdd9.join(userRdd6.mapToPair((PairFunction<Tuple2<Tuple2<Long, Long>, Integer>, Long, Integer>) f -> new Tuple2<>(f._1._1,f._2)));
        JavaRDD<Tuple5<Long,Long,Integer,Integer,Integer>> userRdd11=userRdd10.map((Function<Tuple2<Long, Tuple2<Tuple4<Long, Long, Integer, Integer>, Integer>>, Tuple5<Long, Long, Integer, Integer, Integer>>) f -> new Tuple5<>(f._2._1._1(),f._2._1._2(),f._2._1._3(),f._2._1._4(),f._2._2));
        JavaRDD<Tuple3<Long,Long,Double>> userRdd12=userRdd11.map((Function<Tuple5<Long, Long, Integer, Integer, Integer>, Tuple3<Long, Long, Double>>) f -> new Tuple3<>(f._1(),f._2(),(f._3()/Math.sqrt(f._4()*f._5()))));

        return userRdd12.map((Function<Tuple3<Long, Long, Double>, ItemSemi>) f -> new ItemSemi(f._1(),f._2(),f._3())).collect();
    }
}
