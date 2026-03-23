package org.example.petstorespring.vo;

import lombok.Data;
import org.example.petstorespring.entity.Item;
import org.example.petstorespring.entity.Product;

@Data
public class ItemVO {
    private String itemId;
    private String productId;
    private String categoryId;
    private String productName;
    private String listPrice;
    private String unitCost;
    private String status;
    private String attribution1;
    private String attribution2;
    private String attribution3;
    private String attribution4;
    private String attribution5;
}