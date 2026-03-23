package org.example.petstorespring.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.petstorespring.entity.Product;
import org.springframework.stereotype.Repository;

@Repository()
public interface ProductMapper extends BaseMapper<Product> {
}
