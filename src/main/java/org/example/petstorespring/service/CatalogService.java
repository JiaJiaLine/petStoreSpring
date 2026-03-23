package org.example.petstorespring.service;

import org.example.petstorespring.vo.CategoryVO;
import org.example.petstorespring.vo.ProductVO;

public interface CatalogService {
    public CategoryVO getCategory(String categoryId);
    public ProductVO getProduct(String productId);
}
