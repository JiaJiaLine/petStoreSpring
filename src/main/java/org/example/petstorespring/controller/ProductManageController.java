package org.example.petstorespring.controller;

import org.example.petstorespring.entity.Category;
import org.example.petstorespring.entity.Product;
import org.example.petstorespring.entity.Item;
import org.example.petstorespring.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/manage")
public class ProductManageController {

    @Autowired
    private CatalogService catalogService;

    @GetMapping("/index")
    public String manageIndex(Model model) {
        model.addAttribute("categoryList", catalogService.getAllCategories());
        model.addAttribute("productList", catalogService.getAllProducts());
        model.addAttribute("itemList", catalogService.getAllItems());
        return "manage/manage";
    }

    @PostMapping("/addCategory")
    public String addCategory(Category category) {
        catalogService.addCategory(category);
        return "redirect:/manage/index";
    }

    @PostMapping("/updateCategory")
    public String updateCategory(Category category) {
        catalogService.updateCategory(category);
        return "redirect:/manage/index";
    }

    @GetMapping("/editCategory")
    public String editCategory(@RequestParam String categoryId, Model model) {
        model.addAttribute("category", catalogService.getCategory(categoryId));
        return "manage/editCategory";
    }

    @GetMapping("/deleteCategory")
    public String deleteCategory(@RequestParam String categoryId) {
        catalogService.deleteCategory(categoryId);
        return "redirect:/manage/index";
    }

    @PostMapping("/addProduct")
    public String addProduct(Product product, Model model) {
        if (catalogService.getCategory(product.getCategoryId()) == null) {
            model.addAttribute("error", "分类ID不存在：" + product.getCategoryId());
            model.addAttribute("categoryList", catalogService.getAllCategories());
            model.addAttribute("productList", catalogService.getAllProducts());
            model.addAttribute("itemList", catalogService.getAllItems());
            return "manage/manage";
        }
        catalogService.addProduct(product);
        return "redirect:/manage/index";
    }

    @PostMapping("/updateProduct")
    public String updateProduct(Product product, Model model) {
        if (catalogService.getCategory(product.getCategoryId()) == null) {
            model.addAttribute("product", catalogService.getProduct(product.getProductId()));
            model.addAttribute("error", "分类ID不存在：" + product.getCategoryId());
            return "manage/editProduct";
        }
        catalogService.updateProduct(product);
        return "redirect:/manage/index";
    }

    @GetMapping("/editProduct")
    public String editProduct(@RequestParam String productId, Model model) {
        model.addAttribute("product", catalogService.getProduct(productId));
        return "manage/editProduct";
    }

    @GetMapping("/deleteProduct")
    public String deleteProduct(@RequestParam String productId) {
        catalogService.deleteProduct(productId);
        return "redirect:/manage/index";
    }

    @PostMapping("/addItem")
    public String addItem(Item item, Model model) {
        if (catalogService.getProduct(item.getProductId()) == null) {
            model.addAttribute("error", "商品ID不存在：" + item.getProductId());
            model.addAttribute("categoryList", catalogService.getAllCategories());
            model.addAttribute("productList", catalogService.getAllProducts());
            model.addAttribute("itemList", catalogService.getAllItems());
            return "manage/manage";
        }
        if (item.getStatus() == null || (!item.getStatus().equals("P") && !item.getStatus().equals("N"))) {
            model.addAttribute("error", "状态只能填 P（上架）或 N（下架）");
            model.addAttribute("categoryList", catalogService.getAllCategories());
            model.addAttribute("productList", catalogService.getAllProducts());
            model.addAttribute("itemList", catalogService.getAllItems());
            return "manage/manage";
        }
        catalogService.addItem(item);
        return "redirect:/manage/index";
    }

    @PostMapping("/updateItem")
    public String updateItem(Item item, Model model) {
        if (catalogService.getProduct(item.getProductId()) == null) {
            model.addAttribute("item", catalogService.getItem(item.getItemId()));
            model.addAttribute("error", "商品ID不存在：" + item.getProductId());
            return "manage/editItem";
        }
        catalogService.updateItem(item);
        return "redirect:/manage/index";
    }

    @GetMapping("/editItem")
    public String editItem(@RequestParam String itemId, Model model) {
        model.addAttribute("item", catalogService.getItem(itemId));
        return "manage/editItem";
    }

    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam String itemId) {
        catalogService.deleteItem(itemId);
        return "redirect:/manage/index";
    }

    @GetMapping("/putItemOnSale")
    public String putItemOnSale(@RequestParam String itemId) {
        catalogService.putItemOnSale(itemId);
        return "redirect:/manage/index";
    }
}