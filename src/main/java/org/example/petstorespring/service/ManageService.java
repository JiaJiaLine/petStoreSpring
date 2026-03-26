package org.example.petstorespring.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.petstorespring.entity.Category;
import org.example.petstorespring.vo.CategoryVO;

import java.util.List;

public interface ManageService{
    // 1. 获取所有分类（用于初始页面展示）
    List<Category> getAllCategories();

    // 2. 根据关键字搜索分类
    List<Category> searchCategories(String keyword);

    // 3. 安全删除分类（带子产品校验）
    // 返回值：删除成功返回 true，如果有子产品不能删除返回 false
    boolean deleteCategorySafe(String categoryId);

    Category getCategoryById(String categoryId); // 回显修改页面用的
    void addCategory(Category category);         // 新增保存
    void updateCategory(Category category);      // 修改保存
}
