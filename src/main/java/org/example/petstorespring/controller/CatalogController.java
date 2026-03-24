package org.example.petstorespring.controller;

import org.example.petstorespring.service.CatalogService;
import org.example.petstorespring.vo.CategoryVO;
import org.example.petstorespring.vo.ItemVO;
import org.example.petstorespring.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/catalog")
public class CatalogController {

    @Autowired
    private CatalogService catalogService;

    @GetMapping("/index")
    public String CatalogController(){
        return "catalog/main";
    }

    @GetMapping("/viewCategory")
    public String CategoryController(@RequestParam String categoryId, Model model){
        CategoryVO categoryVO = catalogService.getCategory(categoryId);
        model.addAttribute("category",categoryVO);
        return "catalog/category";
    }

    @GetMapping("/viewProduct")
    public String ProductController(@RequestParam String productId,Model model){
        ProductVO productVO = catalogService.getProduct(productId);
        model.addAttribute("product",productVO);
        return "catalog/product";
    }

    @GetMapping("/viewItem")
    public String ItemController(@RequestParam String itemId,Model model){
        ItemVO itemVO = catalogService.getItem(itemId);
        model.addAttribute("item",itemVO);
        return "catalog/item";
    }
}
