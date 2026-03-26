package org.example.petstorespring.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.petstorespring.entity.Order;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    // 不需要自己写 getAllOrders() 了，BaseMapper 已经提供了通用查询方法
    public List<Order> getAllOrders();
}