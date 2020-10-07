package cn.huangchengxi.mllib.repos;

import cn.huangchengxi.mllib.beans.ItemSemi;
import cn.huangchengxi.mllib.conf.ItemSimilarity;
import cn.huangchengxi.mllib.conf.OccurrenceSimilarity;
import cn.huangchengxi.mllib.entities.ItemPref;
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
public class ItemCFRepo {
    @Autowired
    ItemPrefRepository itemPrefRepository;
    @Autowired
    JavaSparkContext jsc;

    private ItemCFRepo(){}

    static List<ItemSemi> findByItemSemi(SemiType type){
        switch (type){
            case OCCURRENCE:
                return new OccurrenceSimilarity().calculateResult();
            case COSINE:
                //return new CosineSimilarity().calculateResult();
            case EUCLIDEAN:
                //return new EuclideanSimilarity().calculateResult();
            default:
                return null;
        }
    }
    enum SemiType{
        OCCURRENCE,
        COSINE,
        EUCLIDEAN
    }
}
