package org.example.petstorespring.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petstorespring.entity.Category;
import org.example.petstorespring.entity.Item;
import org.example.petstorespring.entity.Product;
import org.example.petstorespring.service.ManageService;
import org.example.petstorespring.vo.ItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/manage")
public class ManageController {
    @Autowired
    private ManageService manageService;

    // 1. 进入 Category 管理主页
    @GetMapping("/manageCategory")
    public String manageCategory(Model model, HttpSession session) {
        String user = manageService.openUser(session);
        if(user != "manager"){
            model.addAttribute("errorMsg", "you're not allow here!!!!!");
            return "manage/error";
        }else {
            List<Category> categoryList = manageService.getAllCategories();
            model.addAttribute("categoryList", categoryList);
            return "manage/categorySearch"; // 假设你的 HTML 放在 templates/manage 文件夹下
        }
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
    public String deleteCategory(@RequestParam("categoryId") String categoryId,
                                 RedirectAttributes redirectAttributes,
                                 HttpSession session,
                                 Model model) {
        String user = manageService.openUser(session);
        if(user != "manager"){
            model.addAttribute("errorMsg", "you're not allow here");
            return "manage/error";
        }else {
            boolean isDeleted = manageService.deleteCategorySafe(categoryId);

            if (isDeleted) {
                // 删除成功，使用 RedirectAttributes 传递成功提示
                redirectAttributes.addFlashAttribute("msg", "Successfully deleted category: " + categoryId);
                redirectAttributes.addFlashAttribute("msgType", "Success");
            } else {
                // 删除失败（有子产品），传递错误提示
                redirectAttributes.addFlashAttribute("msg", "Cannot delete! There are still products under category: " + categoryId);
                redirectAttributes.addFlashAttribute("msgType", "Error");
            }

            // 删除完重定向回主页，防止刷新表单重复提交
            return "redirect:/manage/manageCategory";
        }
    }

    // 1. 跳转到新增页面
    @GetMapping("/addCategoryPage")
    public String addCategoryPage(HttpSession session,Model model) {
        String user = manageService.openUser(session);
        if(user != "manager"){
            model.addAttribute("errorMsg", "you're not allow here");
            return "manage/error";
        }else {
            return "manage/addCategory"; // 跳转到 templates/manage/addCategory.html
        }
    }

    // 2. 接收新增表单并保存
    @PostMapping("/addCategory")
    public String addCategory(Category category, RedirectAttributes redirectAttributes) {
        manageService.addCategory(category);
        redirectAttributes.addFlashAttribute("msg", "Successfully added new category!");
        redirectAttributes.addFlashAttribute("msgType", "Success");
        return "redirect:/manage/manageCategory"; // 保存完跳回列表页
    }

    // ================== 修改 Category 模块 ==================

    // 1. 跳转到修改页面 (需要带着原数据去回显)
    @GetMapping("/editCategoryPage")
    public String editCategoryPage(@RequestParam("categoryId") String categoryId, Model model, HttpSession session) {
        String user = manageService.openUser(session);
        if(user != "manager"){
            model.addAttribute("errorMsg", "you're not allow here");
            return "manage/error";
        }else {
            Category category = manageService.getCategoryById(categoryId);
            model.addAttribute("category", category);
            return "manage/editCategory"; // 跳转到 templates/manage/editCategory.html
        }
    }

    // 2. 接收修改表单并保存
    @PostMapping("/editCategory")
    public String editCategory(Category category, RedirectAttributes redirectAttributes) {
        manageService.updateCategory(category);
        redirectAttributes.addFlashAttribute("msg", "Successfully updated category!");
        redirectAttributes.addFlashAttribute("msgType", "Success");
        return "redirect:/manage/manageCategory"; // 保存完跳回列表页
    }

    // ================== Product 模块 ==================

    // 1. 进入 Product 管理主页
    @GetMapping("/manageProduct")
    public String manageProduct(Model model, HttpSession session) {
        String user = manageService.openUser(session);
        if(user != "manager"){
            model.addAttribute("errorMsg", "you're not allow here");
            return "manage/error";
        }else {
            // 🌟 重点：需要同时传 分类列表(给下拉框) 和 产品列表(给表格)
            model.addAttribute("categoryList", manageService.getAllCategories());
            model.addAttribute("productList", manageService.getAllProducts());
            return "manage/productSearch";
        }
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
    public String deleteProduct(@RequestParam("productId") String productId,
                                RedirectAttributes redirectAttributes,
                                HttpSession session,
                                Model model) {
        String user = manageService.openUser(session);
        if(user != "manager"){
            model.addAttribute("errorMsg", "you're not allow here");
            return "manage/error";
        }else {
            boolean isDeleted = manageService.deleteProductSafe(productId);
            if (isDeleted) {
                redirectAttributes.addFlashAttribute("msg", "Successfully deleted product: " + productId);
                redirectAttributes.addFlashAttribute("msgType", "Success");
            } else {
                redirectAttributes.addFlashAttribute("msg", "Cannot delete! There are still items under product: " + productId);
                redirectAttributes.addFlashAttribute("msgType", "Error");
            }
            return "redirect:/manage/manageProduct";
        }
    }

    // ================== 新增 Product 模块 ==================

    @GetMapping("/addProductPage")
    public String addProductPage(Model model, HttpSession session) {
        String user = manageService.openUser(session);
        if(user != "manager"){
            model.addAttribute("errorMsg", "you're not allow here");
            return "manage/error";
        }else {
            // 🚨 核心：把所有分类查出来，传给前端渲染下拉框！
            model.addAttribute("categoryList", manageService.getAllCategories());
            return "manage/addProduct";
        }
    }

    @PostMapping("/addProduct")
    public String addProduct(Product product, RedirectAttributes redirectAttributes) {
        manageService.addProduct(product);
        redirectAttributes.addFlashAttribute("msg", "Successfully added new product!");
        redirectAttributes.addFlashAttribute("msgType", "Success");
        return "redirect:/manage/manageProduct"; // 成功后跳回列表页
    }

    // ================== 修改 Product 模块 ==================

    @GetMapping("/editProductPage")
    public String editProductPage(@RequestParam("productId") String productId, Model model, HttpSession session) {
        String user = manageService.openUser(session);
        if(user != "manager"){
            model.addAttribute("errorMsg", "you're not allow here");
            return "manage/error";
        }else {
        // 1. 查出当前产品的信息，用于回显填满输入框
        model.addAttribute("product", manageService.getProductById(productId));
        // 2. 🚨 同样必须查出所有分类，用于渲染下拉框！
        model.addAttribute("categoryList", manageService.getAllCategories());

        return "manage/editProduct";
        }
    }

    @PostMapping("/editProduct")
    public String editProduct(Product product, RedirectAttributes redirectAttributes) {
        manageService.updateProduct(product);
        redirectAttributes.addFlashAttribute("msg", "Successfully updated product!");
        redirectAttributes.addFlashAttribute("msgType", "Success");
        return "redirect:/manage/manageProduct";
    }

    // ================== Item 模块：级联 AJAX 接口 ==================

    @GetMapping("/getProductsByCategory")
    @ResponseBody // 🌟 极其重要！它告诉 Spring：不要去找 HTML，直接把查到的 List 变成 JSON 数据扔给前端！
    public List<Product> getProductsByCategory(@RequestParam("categoryId") String categoryId, HttpSession session) {
    return manageService.searchProducts("",categoryId);
    }

    // ================== Item 模块：查询页面 ==================

    @GetMapping("/manageItem")
    public String manageItem(Model model, HttpSession session) {
        String user = manageService.openUser(session);
        if(user != "manager"){
            model.addAttribute("errorMsg", "you're not allow here");
            return "manage/error";
        }else {
            model.addAttribute("categoryList", manageService.getAllCategories());
            // 初始页面：什么条件都不传，查出所有的 ItemVO 兜底展示
            model.addAttribute("itemList", manageService.searchItems(null, null, null));
            return "manage/itemSearch";
        }
    }

    @PostMapping("/searchItem")
    public String searchItem(@RequestParam(value = "categoryId", required = false) String categoryId,
                             @RequestParam(value = "productId", required = false) String productId,
                             @RequestParam(value = "keyword", required = false) String keyword,
                             Model model) {

        // 调用我们刚刚写好的超级组装方法
        List<ItemVO> itemVOList = manageService.searchItems(categoryId, productId, keyword);

        // 渲染页面需要的核心数据
        model.addAttribute("categoryList", manageService.getAllCategories());
        model.addAttribute("itemList", itemVOList);

        // 回显用户的搜索条件（为了让页面刷新后，下拉框依然保持选中的状态）
        model.addAttribute("currentCategoryId", categoryId);
        model.addAttribute("currentProductId", productId);
        model.addAttribute("currentKeyword", keyword);

        return "manage/itemSearch";
    }

    @GetMapping("/toggleItemStatus")
    public String toggleItemStatus(@RequestParam("itemId") String itemId,
                                   @RequestParam("currentStatus") String currentStatus,
                                   RedirectAttributes redirectAttributes,
                                   HttpSession session,
                                   Model model) {
        String user = manageService.openUser(session);
        if(user != "manager"){
            model.addAttribute("errorMsg", "you're not allow here");
            return "manage/error";
        }else {
            manageService.toggleItemStatus(itemId, currentStatus);

            // 动态提示信息
            String action = "P".equals(currentStatus) ? "Delisted" : "Listed";
            redirectAttributes.addFlashAttribute("msg", "Item " + itemId + " has been " + action + " successfully!");
            redirectAttributes.addFlashAttribute("msgType", "Success");

            return "redirect:/manage/manageItem"; // 完事跳回列表页，你会发现状态变色了
        }
    }

    // ================== 修改库存 (Stock) 模块 ==================

    // 1. 跳转到修改库存页面
    @GetMapping("/editStockPage")
    public String editStockPage(@RequestParam("itemId") String itemId, Model model, HttpSession session) {
        String user = manageService.openUser(session);
        if(user != "manager"){
            model.addAttribute("errorMsg", "you're not allow here");
            return "manage/error";
        }else {
            // 查出这个商品的当前库存，传给页面回显
            ItemVO itemVO = manageService.getItemVOById(itemId);
            model.addAttribute("item", itemVO);
            return "manage/editStock";
        }
    }

    // 2. 接收表单，保存库存
    @PostMapping("/updateStock")
    public String updateStock(@RequestParam("itemId") String itemId,
                              @RequestParam("quantity") Integer quantity,
                              RedirectAttributes redirectAttributes) {
        // 调用 Service 更新 Inventory 表
        manageService.updateStock(itemId, quantity);

        redirectAttributes.addFlashAttribute("msg", "Successfully updated stock for Item: " + itemId);
        redirectAttributes.addFlashAttribute("msgType", "Success");
        return "redirect:/manage/manageItem";
    }

    // ================== Add Item & Delete Item ==================

    // 1. 跳转到新增 SKU 页面
    @GetMapping("/addItemPage")
    public String addItemPage(Model model, HttpSession session) {
        String user = manageService.openUser(session);
        if(user != "manager"){
            model.addAttribute("errorMsg", "you're not allow here");
            return "manage/error";
        }else {
            // 🌟 只需要查出 Category，Product 下拉框交给咱们写好的 AJAX 去动态加载！
            model.addAttribute("categoryList", manageService.getAllCategories());
            return "manage/addItem";
        }
    }

    // 2. 接收表单，保存新 SKU
    @PostMapping("/addItem")
    public String addItem(Item item,
                          @RequestParam("quantity") Integer quantity,
                          RedirectAttributes redirectAttributes,int supplier,
                          HttpSession session) {
        manageService.addItem(item, quantity,supplier);
        redirectAttributes.addFlashAttribute("msg", "Successfully added new SKU: " + item.getItemId());
        redirectAttributes.addFlashAttribute("msgType", "Success");
        return "redirect:/manage/manageItem";
    }

    // 3. 彻底删除 SKU
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("itemId") String itemId, RedirectAttributes redirectAttributes,
                             HttpSession session,
                             Model model) {
        String user = manageService.openUser(session);
        if(user != "manager"){
            model.addAttribute("errorMsg", "you're not allow here");
            return "manage/error";
        }else {
            manageService.deleteItem(itemId);
            redirectAttributes.addFlashAttribute("msg", "Successfully deleted SKU: " + itemId);
            redirectAttributes.addFlashAttribute("msgType", "Success");
            return "redirect:/manage/manageItem";
        }
    }
}
