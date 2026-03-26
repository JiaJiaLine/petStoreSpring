package org.example.petstorespring.controller;

import org.example.petstorespring.entity.Category;
import org.example.petstorespring.entity.Product;
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

    // ================== Product 模块 ==================

    // 1. 进入 Product 管理主页
    @GetMapping("/manageProduct")
    public String manageProduct(Model model) {
        // 🌟 重点：需要同时传 分类列表(给下拉框) 和 产品列表(给表格)
        model.addAttribute("categoryList", manageService.getAllCategories());
        model.addAttribute("productList", manageService.getAllProducts());
        return "manage/productSearch";
    }

    // 2. 组合搜索表单提交
    @PostMapping("/searchProduct")
    public String searchProduct(@RequestParam(value = "keyword", required = false) String keyword,
                                @RequestParam(value = "categoryId", required = false) String categoryId,
                                Model model) {
        // 查询出符合条件的产品
        List<Product> productList = manageService.searchProducts(keyword, categoryId);

        // 🌟 依然要把分类列表传过去，不然搜索完下拉框就空了！
        model.addAttribute("categoryList", manageService.getAllCategories());
        model.addAttribute("productList", productList);

        // 贴心小功能：把用户刚才选的条件传回前端，让下拉框保持选中状态
        model.addAttribute("currentKeyword", keyword);
        model.addAttribute("currentCategoryId", categoryId);

        return "manage/productSearch";
    }

    // 3. 安全删除操作
    @GetMapping("/deleteProduct")
    public String deleteProduct(@RequestParam("productId") String productId, RedirectAttributes redirectAttributes) {
        boolean isDeleted = manageService.deleteProductSafe(productId);
        if (isDeleted) {
            redirectAttributes.addFlashAttribute("msg", "Successfully deleted product: " + productId);
        } else {
            redirectAttributes.addFlashAttribute("msg", "Cannot delete! There are still items under product: " + productId);
        }
        return "redirect:/manage/manageProduct";
    }

    // ================== 新增 Product 模块 ==================

    @GetMapping("/addProductPage")
    public String addProductPage(Model model) {
        // 🚨 核心：把所有分类查出来，传给前端渲染下拉框！
        model.addAttribute("categoryList", manageService.getAllCategories());
        return "manage/addProduct";
    }

    @PostMapping("/addProduct")
    public String addProduct(Product product, RedirectAttributes redirectAttributes) {
        manageService.addProduct(product);
        redirectAttributes.addFlashAttribute("msg", "Successfully added new product!");
        return "redirect:/manage/manageProduct"; // 成功后跳回列表页
    }

    // ================== 修改 Product 模块 ==================

    @GetMapping("/editProductPage")
    public String editProductPage(@RequestParam("productId") String productId, Model model) {
        // 1. 查出当前产品的信息，用于回显填满输入框
        model.addAttribute("product", manageService.getProductById(productId));
        // 2. 🚨 同样必须查出所有分类，用于渲染下拉框！
        model.addAttribute("categoryList", manageService.getAllCategories());

        return "manage/editProduct";
    }

    @PostMapping("/editProduct")
    public String editProduct(Product product, RedirectAttributes redirectAttributes) {
        manageService.updateProduct(product);
        redirectAttributes.addFlashAttribute("msg", "Successfully updated product!");
        return "redirect:/manage/manageProduct";
    }
}
