package org.example.petstorespring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/index")
public class CatalogController {
    @GetMapping("/main")
    public String CatalogController(){
        return "catalog/main";
    }
}
