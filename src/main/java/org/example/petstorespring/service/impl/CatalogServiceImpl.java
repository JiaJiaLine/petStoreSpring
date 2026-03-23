package org.example.petstorespring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.petstorespring.entity.Category;
import org.example.petstorespring.entity.Product;
import org.example.petstorespring.persistence.CategoryMapper;
import org.example.petstorespring.persistence.ProductMapper;
import org.example.petstorespring.service.CatalogService;
import org.example.petstorespring.vo.CategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("catalogService")
public class CatalogServiceImpl implements CatalogService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ProductMapper productMapper;
    @Override
    public CategoryVO getCategory(String categoryId) {
        CategoryVO categoryVO = new CategoryVO();
        Category category = categoryMapper.selectById(categoryId);

        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category",categoryId);
        List<Product> productList = productMapper.selectList(queryWrapper);

        categoryVO.setCategoryId(categoryId);
        categoryVO.setProductList(productList);
        categoryVO.setCategoryName(category.getName());

        return categoryVO;
    }
}
