package org.example.petstorespring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.petstorespring.entity.Category;
import org.example.petstorespring.entity.Inventory;
import org.example.petstorespring.entity.Item;
import org.example.petstorespring.entity.Product;
import org.example.petstorespring.persistence.CategoryMapper;
import org.example.petstorespring.persistence.InventoryMapper;
import org.example.petstorespring.persistence.ItemMapper;
import org.example.petstorespring.persistence.ProductMapper;
import org.example.petstorespring.service.CatalogService;
import org.example.petstorespring.vo.CategoryVO;
import org.example.petstorespring.vo.ItemVO;
import org.example.petstorespring.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("catalogService")
public class CatalogServiceImpl implements CatalogService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private InventoryMapper inventoryMapper;

    @Override
    public CategoryVO getCategory(String categoryId) {
        CategoryVO categoryVO = new CategoryVO();
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            return categoryVO;
        }

        LambdaQueryWrapper<Product> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Product::getCategoryId,categoryId);
        List<Product> productList = productMapper.selectList(lambdaQueryWrapper);

        categoryVO.setCategoryId(categoryId);
        categoryVO.setProductList(productList);
        categoryVO.setCategoryName(category.getName());

        return categoryVO;
    }

    @Override
    public ProductVO getProduct(String productId){
        ProductVO productVO = new ProductVO();
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return productVO;
        }
        Category category = categoryMapper.selectById(product.getCategoryId());

        LambdaQueryWrapper<Item> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Item::getProductId,productId);
        List<Item> itemList = itemMapper.selectList(lambdaQueryWrapper);

        productVO.setProductId(productId);
        productVO.setProductName(product.getName());
        productVO.setCategoryId(category.getCategoryId());
        productVO.setCategoryName(category.getName());
        productVO.setItemList(itemList);
        return productVO;
    }

    @Override
    public ItemVO getItem(String itemId) {
        ItemVO itemVO = new ItemVO();
        Item item = itemMapper.selectById(itemId);
        Inventory inventory = inventoryMapper.selectById(itemId);
        Product product = productMapper.selectById(item.getProductId());

        String descn = product.getDescription();
        if (descn != null && !descn.isEmpty()){
            // 1. 匹配图片的正则
            Pattern pattern = Pattern.compile("<image src=\"([^\"]+)\">");
            Matcher matcher = pattern.matcher(descn);
            if (matcher.find()) {
                // 拿到第一个括号里捕获的内容：images/bird4.gif
                String rawPath = matcher.group(1);
                String cleanPath = rawPath.replace("../", "");
                itemVO.setDescriptionImage(cleanPath);

                // 拿到 > 之后的文字部分并去掉两端空格
                String text = descn.substring(matcher.end()).trim();
                itemVO.setDescriptionText(text);
            } else {
                // 如果没匹配到图片标签，就当做纯文本处理
                itemVO.setDescriptionText(descn);
            }
        }

        itemVO.setAttribute1(item.getAttribute1());
        itemVO.setAttribute2(item.getAttribute2());
        itemVO.setAttribute3(item.getAttribute3());
        itemVO.setAttribute4(item.getAttribute4());
        itemVO.setAttribute5(item.getAttribute5());
        itemVO.setItemId(item.getItemId());
        itemVO.setListPrice(item.getListPrice());
        itemVO.setQuantity(inventory.getQuantity());
        itemVO.setProductId(product.getProductId());
        itemVO.setProductName(product.getName());
        itemVO.setCategoryId(product.getCategoryId());

        return itemVO;
    }
}
