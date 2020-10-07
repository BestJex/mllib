package cn.huangchengxi.mllib.conf;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spark")
public class SparkConfBean {
    private String sparkHome = "1";

    private String appName = "sparkTest";

    private String master = "local";

    @Bean
    @ConditionalOnMissingBean(SparkConf.class)
    public SparkConf sparkConf() {
        SparkConf conf = new SparkConf();
        conf.setAppName(appName);
        conf.setMaster(master);
        return conf;
    }

    @Bean
    @ConditionalOnMissingBean(JavaSparkContext.class)
    public JavaSparkContext javaSparkContext() {
        return new JavaSparkContext(sparkConf());
    }

    public String getSparkHome() {
        return sparkHome;
    }

    public void setSparkHome(String sparkHome) {
        this.sparkHome = sparkHome;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }
}
