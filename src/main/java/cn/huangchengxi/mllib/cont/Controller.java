package cn.huangchengxi.mllib.cont;

import cn.huangchengxi.mllib.conf.OccurrenceSim;
import cn.huangchengxi.mllib.conf.OccurrenceSimilarity;
import cn.huangchengxi.mllib.entities.ItemEntity;
import cn.huangchengxi.mllib.entities.ItemPref;
import cn.huangchengxi.mllib.entities.UserEntity;
import cn.huangchengxi.mllib.repos.ItemCFRepo;
import cn.huangchengxi.mllib.repos.ItemPrefRepository;
import cn.huangchengxi.mllib.repos.ItemRepo;
import cn.huangchengxi.mllib.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RestController
public class Controller {
    @Autowired
    UserRepo userRepo;
    @Autowired
    ItemRepo itemRepo;
    @Autowired
    OccurrenceSim occurrenceSim;
    @Autowired
    ItemPrefRepository itemPrefRepository;

    @RequestMapping("/addUser")
    public String addUsers(){
        for(int i=0;i<20;i++){
            userRepo.save(new UserEntity("user"+i));
        }
        return "Success";
    }
    @RequestMapping("/addItem")
    public String addItem(){
        for (int i=0;i<20;i++){
            itemRepo.save(new ItemEntity("item"+i));
        }
        return "Success";
    }
    @RequestMapping("/addPref")
    public String addPref(){
        List<UserEntity> users=userRepo.findAll();
        List<ItemEntity> items=itemRepo.findAll();

        for (UserEntity u:users){
            ItemPref pref=new ItemPref();
            pref.uid=u.uid;
            pref.starPref=randPref();
            pref.itemId=items.get(randIndex(items.size())).iid;
            itemPrefRepository.save(pref);
        }
        return "Success";
    }
    private int randIndex(int max){
        Random random=new Random();
        return random.nextInt(max);
    }
    private double randPref(){
        Random random=new Random();
        return random.nextDouble()*10;
    }
    @RequestMapping("/pullResult")
    public String pullResult(){
        return Arrays.toString(occurrenceSim.occurrenceSimilarity());
    }
}
