package org.example.petstorespring.controller;

import org.example.petstorespring.service.CatalogService;
import org.example.petstorespring.vo.CategoryVO;
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
    public String ProductController(){
        return "catalog/product";
    }
}
