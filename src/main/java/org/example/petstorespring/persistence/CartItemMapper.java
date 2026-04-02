package org.example.petstorespring.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.petstorespring.entity.CartItem;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemMapper extends BaseMapper<CartItem> {
}
