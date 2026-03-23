package org.example.petstorespring.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.petstorespring.entity.Item;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemMapper extends BaseMapper<Item> {
}
