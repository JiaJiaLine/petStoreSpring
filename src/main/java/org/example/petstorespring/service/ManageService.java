package org.example.petstorespring.service;

import org.example.petstorespring.entity.Category;
import org.example.petstorespring.entity.Item;
import org.example.petstorespring.entity.Product;
import org.example.petstorespring.vo.CategoryVO;
import org.example.petstorespring.vo.ProductVO;

import java.util.List;

public interface ManageService {
    CategoryVO getCategory(String categoryId);
    void addCategory(Category category);
    void updateCategory(Category category);
    void deleteCategory(String categoryId);

    ProductVO getProduct(String productId);
    void addProduct(Product product);
    void updateProduct(Product product);
    void deleteProduct(String productId);

    Item getItem(String itemId);
    void addItem(Item item);
    void updateItem(Item item);
    void deleteItem(String itemId);
    void putItemOnSale(String itemId);

    List<Category> getAllCategories();
    List<Product> getAllProducts();
    List<Item> getAllItems();
}
