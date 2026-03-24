package org.example.petstorespring.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.petstorespring.entity.Inventory;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryMapper extends BaseMapper<Inventory> {
}
