package org.example.petstorespring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.petstorespring.entity.Category;
import org.example.petstorespring.entity.Product;
import org.example.petstorespring.persistence.CategoryMapper;
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
}
