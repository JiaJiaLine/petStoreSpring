package org.example.petstorespring.service;

import org.example.petstorespring.vo.CategoryVO;
import org.example.petstorespring.vo.ProductVO;
import org.example.petstorespring.vo.ItemVO;
import org.example.petstorespring.entity.Category;
import org.example.petstorespring.entity.Product;
import org.example.petstorespring.entity.Item;

import java.util.List;

public interface CatalogService {
    public CategoryVO getCategory(String categoryId);
    void addCategory(Category category);
    void updateCategory(Category category);
    void deleteCategory(String categoryId);

    public ProductVO getProduct(String productId);
    void addProduct(Product product);
    void updateProduct(Product product);
    void deleteProduct(String productId);

    public ItemVO getItem(String itemId);
    void addItem(Item item);
    void updateItem(Item item);
    void deleteItem(String itemId);
    void putItemOnSale(String itemId);

    List<Category> getAllCategories();
    List<Product> getAllProducts();
    List<Item> getAllItems();
}
