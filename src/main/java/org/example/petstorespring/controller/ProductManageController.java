package org.example.petstorespring.controller;

import org.example.petstorespring.entity.Category;
import org.example.petstorespring.entity.Product;
import org.example.petstorespring.entity.Item;
import org.example.petstorespring.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/manage")
public class ProductManageController {

    @Autowired
    private ManageService manageService;

    @GetMapping("/index")
    public String manageIndex(Model model) {
        model.addAttribute("categoryList", manageService.getAllCategories());
        model.addAttribute("productList", manageService.getAllProducts());
        model.addAttribute("itemList", manageService.getAllItems());
        return "manage/manage";
    }

    @PostMapping("/addCategory")
    public String addCategory(Category category) {
        manageService.addCategory(category);
        return "redirect:/manage/index";
    }

    @PostMapping("/updateCategory")
    public String updateCategory(Category category) {
        manageService.updateCategory(category);
        return "redirect:/manage/index";
    }

    @GetMapping("/editCategory")
    public String editCategory(@RequestParam String categoryId, Model model) {
        model.addAttribute("category", manageService.getCategory(categoryId));
        return "manage/editCategory";
    }

    @GetMapping("/deleteCategory")
    public String deleteCategory(@RequestParam String categoryId) {
        manageService.deleteCategory(categoryId);
        return "redirect:/manage/index";
    }

    @PostMapping("/addProduct")
    public String addProduct(Product product, Model model) {
        if (manageService.getCategory(product.getCategoryId()) == null) {
            model.addAttribute("error", "分类ID不存在：" + product.getCategoryId());
            model.addAttribute("categoryList", manageService.getAllCategories());
            model.addAttribute("productList", manageService.getAllProducts());
            model.addAttribute("itemList", manageService.getAllItems());
            return "manage/manage";
        }
        manageService.addProduct(product);
        return "redirect:/manage/index";
    }

    @PostMapping("/updateProduct")
    public String updateProduct(Product product, Model model) {
        if (manageService.getCategory(product.getCategoryId()) == null) {
            model.addAttribute("product", manageService.getProduct(product.getProductId()));
            model.addAttribute("error", "分类ID不存在：" + product.getCategoryId());
            return "manage/editProduct";
        }
        manageService.updateProduct(product);
        return "redirect:/manage/index";
    }

    @GetMapping("/editProduct")
    public String editProduct(@RequestParam String productId, Model model) {
        model.addAttribute("product", manageService.getProduct(productId));
        return "manage/editProduct";
    }

    @GetMapping("/deleteProduct")
    public String deleteProduct(@RequestParam String productId) {
        manageService.deleteProduct(productId);
        return "redirect:/manage/index";
    }

    @PostMapping("/addItem")
    public String addItem(Item item, Model model) {
        if (manageService.getProduct(item.getProductId()) == null) {
            model.addAttribute("error", "商品ID不存在：" + item.getProductId());
            model.addAttribute("categoryList", manageService.getAllCategories());
            model.addAttribute("productList", manageService.getAllProducts());
            model.addAttribute("itemList", manageService.getAllItems());
            return "manage/manage";
        }
        if (item.getStatus() == null || (!item.getStatus().equals("P") && !item.getStatus().equals("N"))) {
            model.addAttribute("error", "状态只能填 P（上架）或 N（下架）");
            model.addAttribute("categoryList", manageService.getAllCategories());
            model.addAttribute("productList", manageService.getAllProducts());
            model.addAttribute("itemList", manageService.getAllItems());
            return "manage/manage";
        }
        manageService.addItem(item);
        return "redirect:/manage/index";
    }

    @PostMapping("/updateItem")
    public String updateItem(Item item, Model model) {
        if (manageService.getProduct(item.getProductId()) == null) {
            model.addAttribute("item", manageService.getItem(item.getItemId()));
            model.addAttribute("error", "商品ID不存在：" + item.getProductId());
            return "manage/editItem";
        }
        manageService.updateItem(item);
        return "redirect:/manage/index";
    }

    @GetMapping("/editItem")
    public String editItem(@RequestParam String itemId, Model model) {
        model.addAttribute("item", manageService.getItem(itemId));
        return "manage/editItem";
    }

    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam String itemId) {
        manageService.deleteItem(itemId);
        return "redirect:/manage/index";
    }

    @GetMapping("/putItemOnSale")
    public String putItemOnSale(@RequestParam String itemId) {
        manageService.putItemOnSale(itemId);
        return "redirect:/manage/index";
    }
}