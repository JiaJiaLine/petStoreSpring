package org.example.petstorespring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.petstorespring.entity.Category;
import org.example.petstorespring.entity.Product;
import org.example.petstorespring.entity.Item;
import org.example.petstorespring.persistence.CategoryMapper;
import org.example.petstorespring.persistence.ProductMapper;
import org.example.petstorespring.persistence.ItemMapper;
import org.example.petstorespring.service.CatalogService;
import org.example.petstorespring.vo.CategoryVO;
import org.example.petstorespring.vo.ProductVO;
import org.example.petstorespring.vo.ItemVO;
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
        if (category == null) return null;

        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category",categoryId);
        List<Product> productList = productMapper.selectList(queryWrapper);

        categoryVO.setCategoryId(categoryId);
        categoryVO.setProductList(productList);
        categoryVO.setCategoryName(category.getName());

        return categoryVO;
    }

    @Override
    public void addCategory(Category category) {
        categoryMapper.insert(category);
    }

    @Override
    public void updateCategory(Category category) {
        categoryMapper.updateById(category);
    }

    @Override
    public void deleteCategory(String categoryId) {
        categoryMapper.deleteById(categoryId);
    }

    @Override
    public ProductVO getProduct(String productId) {
        ProductVO productVO = new ProductVO();
        Product product = productMapper.selectById(productId);
        if (product == null) return null;

        QueryWrapper<Item> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("productid", productId);
        List<Item> itemList = itemMapper.selectList(queryWrapper);

        productVO.setProductId(productId);
        productVO.setProductName(product.getName());
        productVO.setCategoryId(product.getCategoryId());
        productVO.setItemList(itemList);

        return productVO;
    }

    @Override
    public void addProduct(Product product) {
        productMapper.insert(product);
    }

    @Override
    public void updateProduct(Product product) {
        productMapper.updateById(product);
    }

    @Override
    public void deleteProduct(String productId) {
        productMapper.deleteById(productId);
    }

    @Override
    public ItemVO getItem(String itemId) {
        Item item = itemMapper.selectById(itemId);
        Product product = productMapper.selectById(item.getProductId());

        ItemVO itemVO = new ItemVO();
        itemVO.setItemId(itemId);
        itemVO.setProductId(product.getProductId());
        itemVO.setCategoryId(product.getCategoryId());
        itemVO.setProductName(product.getName());
        itemVO.setListPrice(item.getListPrice());
        itemVO.setUnitCost(item.getUnitCost());
        itemVO.setStatus(item.getStatus());
        itemVO.setAttribution1(item.getAttribution1());
        itemVO.setAttribution2(item.getAttribution2());
        itemVO.setAttribution3(item.getAttribution3());
        itemVO.setAttribution4(item.getAttribution4());
        itemVO.setAttribution5(item.getAttribution5());

        return itemVO;
    }

    @Override
    public void addItem(Item item) {
        itemMapper.insert(item);
    }

    @Override
    public void updateItem(Item item) {
        itemMapper.updateById(item);
    }

    @Override
    public void deleteItem(String itemId) {
        itemMapper.deleteById(itemId);
    }

    @Override
    public void putItemOnSale(String itemId) {
        Item item = itemMapper.selectById(itemId);
        item.setStatus("P");
        itemMapper.updateById(item);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.selectList(null);
    }

    @Override
    public List<Product> getAllProducts() {
        return productMapper.selectList(null);
    }

    @Override
    public List<Item> getAllItems() {
        return itemMapper.selectList(null);
    }
}
