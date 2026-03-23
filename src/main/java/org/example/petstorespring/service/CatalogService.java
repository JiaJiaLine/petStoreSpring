package org.example.petstorespring.service;

import org.example.petstorespring.vo.CategoryVO;

public interface CatalogService {
    public CategoryVO getCategory(String categoryId);
}
