package cn.huangchengxi.mllib.repos;

import cn.huangchengxi.mllib.entities.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepo extends JpaRepository<ItemEntity,Long> {
}
