package org.example.petstorespring.controller;

import org.example.petstorespring.entity.Category;
import org.example.petstorespring.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/manage")
public class ManageController {
    @Autowired
    private ManageService manageService;

    // 1. 进入 Category 管理主页
    @GetMapping("/manageCategory")
    public String manageCategory(Model model) {
        List<Category> categoryList = manageService.getAllCategories();
        model.addAttribute("categoryList", categoryList);
        return "manage/categorySearch"; // 假设你的 HTML 放在 templates/manage 文件夹下
    }

    // 2. 搜索表单提交
    @PostMapping("/searchCategory")
    public String searchCategory(@RequestParam("keyword") String keyword, Model model) {
        List<Category> categoryList = manageService.searchCategories(keyword);
        model.addAttribute("categoryList", categoryList);
        // 搜索后依然停留在当前页面
        return "manage/categorySearch";
    }

    // 3. 安全删除操作
    @GetMapping("/deleteCategory")
    public String deleteCategory(@RequestParam("categoryId") String categoryId, RedirectAttributes redirectAttributes) {
        boolean isDeleted = manageService.deleteCategorySafe(categoryId);

        if (isDeleted) {
            // 删除成功，使用 RedirectAttributes 传递成功提示
            redirectAttributes.addFlashAttribute("msg", "Successfully deleted category: " + categoryId);
        } else {
            // 删除失败（有子产品），传递错误提示
            redirectAttributes.addFlashAttribute("msg", "Cannot delete! There are still products under category: " + categoryId);
        }

        // 删除完重定向回主页，防止刷新表单重复提交
        return "redirect:/manage/manageCategory";
    }

    // 1. 跳转到新增页面
    @GetMapping("/addCategoryPage")
    public String addCategoryPage() {
        return "manage/addCategory"; // 跳转到 templates/manage/addCategory.html
    }

    // 2. 接收新增表单并保存
    @PostMapping("/addCategory")
    public String addCategory(Category category, RedirectAttributes redirectAttributes) {
        manageService.addCategory(category);
        redirectAttributes.addFlashAttribute("msg", "Successfully added new category!");
        return "redirect:/manage/manageCategory"; // 保存完跳回列表页
    }

    // ================== 修改 Category 模块 ==================

    // 1. 跳转到修改页面 (需要带着原数据去回显)
    @GetMapping("/editCategoryPage")
    public String editCategoryPage(@RequestParam("categoryId") String categoryId, Model model) {
        Category category = manageService.getCategoryById(categoryId);
        model.addAttribute("category", category);
        return "manage/editCategory"; // 跳转到 templates/manage/editCategory.html
    }

    // 2. 接收修改表单并保存
    @PostMapping("/editCategory")
    public String editCategory(Category category, RedirectAttributes redirectAttributes) {
        manageService.updateCategory(category);
        redirectAttributes.addFlashAttribute("msg", "Successfully updated category!");
        return "redirect:/manage/manageCategory"; // 保存完跳回列表页
    }
}
