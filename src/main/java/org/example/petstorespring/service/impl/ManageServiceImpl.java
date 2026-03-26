package org.example.petstorespring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.petstorespring.entity.Category;
import org.example.petstorespring.entity.Item;
import org.example.petstorespring.entity.Product;
import org.example.petstorespring.persistence.CategoryMapper;
import org.example.petstorespring.persistence.ItemMapper;
import org.example.petstorespring.persistence.ProductMapper;
import org.example.petstorespring.service.ManageService;
import org.example.petstorespring.vo.CategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("manageService")
public class ManageServiceImpl implements ManageService{
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.selectList(null); // 查询全部
    }

    @Override
    public List<Category> searchCategories(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCategories();
        }
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        // 假设你要模糊匹配 Name 或 CategoryId
        wrapper.like(Category::getName, keyword)
                .or()
                .like(Category::getCategoryId, keyword);
        return categoryMapper.selectList(wrapper);
    }

    @Override
    public boolean deleteCategorySafe(String categoryId) {
        // 1. 核心防御：查询 Product 表中是否还有该分类下的产品
        LambdaQueryWrapper<Product> productWrapper = new LambdaQueryWrapper<>();
        productWrapper.eq(Product::getCategoryId, categoryId);

        // 只要查到一条记录，就说明还有子数据，立刻拒绝删除
        if (productMapper.selectCount(productWrapper) > 0) {
            return false;
        }

        // 2. 安全通过，执行删除
        categoryMapper.deleteById(categoryId);
        return true;
    }

    @Override
    public Category getCategoryById(String categoryId) {
        return categoryMapper.selectById(categoryId);
    }

    @Override
    public void addCategory(Category category) {
        // 新增：直接插入
        categoryMapper.insert(category);
    }

    @Override
    public void updateCategory(Category category) {
        // 修改：根据主键 ID 更新其他字段
        categoryMapper.updateById(category);
    }

    @Override
    public List<Product> getAllProducts() {
        return productMapper.selectList(null);
    }

    @Override
    public List<Product> searchProducts(String keyword, String categoryId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        // 1. 如果选了具体的 Category，就加上这个条件
        if (categoryId != null && !categoryId.trim().isEmpty()) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }

        // 2. 如果填了关键字，就模糊匹配 Name 或 ProductId
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Product::getName, keyword)
                    .or()
                    .like(Product::getProductId, keyword));
        }

        return productMapper.selectList(wrapper);
    }

    @Override
    public boolean deleteProductSafe(String productId) {
        // 检查 Item 表里是否还有这个 productId 的数据
        LambdaQueryWrapper<Item> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(Item::getProductId, productId);

        if (itemMapper.selectCount(itemWrapper) > 0) {
            return false; // 还有子商品，拒绝删除！
        }

        productMapper.deleteById(productId);
        return true;
    }

    @Override
    public Product getProductById(String productId) {
        return productMapper.selectById(productId);
    }

    @Override
    public void addProduct(Product product) {
        productMapper.insert(product);
    }

    @Override
    public void updateProduct(Product product) {
        productMapper.updateById(product);
    }
}
