package org.example.petstorespring.vo;

import lombok.Data;
import org.example.petstorespring.entity.Product;

import java.util.List;

@Data
public class CategoryVO {
    private String categoryId;
    private String categoryName;
    private List<Product> productList;
}
