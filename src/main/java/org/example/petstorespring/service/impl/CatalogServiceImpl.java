package org.example.petstorespring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.petstorespring.entity.Category;
import org.example.petstorespring.entity.Item;
import org.example.petstorespring.entity.Product;
import org.example.petstorespring.persistence.CategoryMapper;
import org.example.petstorespring.persistence.ItemMapper;
import org.example.petstorespring.persistence.ProductMapper;
import org.example.petstorespring.service.CatalogService;
import org.example.petstorespring.vo.CategoryVO;
import org.example.petstorespring.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("catalogService")
public class CatalogServiceImpl implements CatalogService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ItemMapper itemMapper;

    @Override
    public CategoryVO getCategory(String categoryId) {
        CategoryVO categoryVO = new CategoryVO();
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            return categoryVO;
        }

        LambdaQueryWrapper<Product> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Product::getCategoryId,categoryId);
        List<Product> productList = productMapper.selectList(lambdaQueryWrapper);

        categoryVO.setCategoryId(categoryId);
        categoryVO.setProductList(productList);
        categoryVO.setCategoryName(category.getName());

        return categoryVO;
    }

    @Override
    public ProductVO getProduct(String productId){
        ProductVO productVO = new ProductVO();
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return productVO;
        }
        Category category = categoryMapper.selectById(product.getCategoryId());

        LambdaQueryWrapper<Item> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Item::getProductId,productId);
        List<Item> itemList = itemMapper.selectList(lambdaQueryWrapper);

        productVO.setProductId(productId);
        productVO.setProductName(product.getName());
        productVO.setCategoryId(category.getCategoryId());
        productVO.setCategoryName(category.getName());
        productVO.setItemList(itemList);
        return productVO;
    }
}
