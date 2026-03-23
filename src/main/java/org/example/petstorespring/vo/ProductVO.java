package org.example.petstorespring.vo;

import lombok.Data;
import org.example.petstorespring.entity.Item;

import java.util.List;

@Data
public class ProductVO {
    private String categoryId;
    private String categoryName;
    private String productId;
    private String productName;
    private List<Item> itemList;
}
