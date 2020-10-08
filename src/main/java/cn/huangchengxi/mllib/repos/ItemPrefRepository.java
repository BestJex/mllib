package cn.huangchengxi.mllib.repos;

import cn.huangchengxi.mllib.entities.ItemPref;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemPrefRepository extends JpaRepository<ItemPref, Long> {
    List<ItemPref> findAllByUid(Long uid);
}
