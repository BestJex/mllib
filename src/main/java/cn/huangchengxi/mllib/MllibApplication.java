package cn.huangchengxi.mllib;

import cn.huangchengxi.mllib.entities.UserEntity;
import cn.huangchengxi.mllib.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MllibApplication {

    public static void main(String[] args) {
        SpringApplication.run(MllibApplication.class, args);
    }
}
