package org.example.petstorespring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.petstorespring.entity.Category;
import org.example.petstorespring.entity.Inventory;
import org.example.petstorespring.entity.Item;
import org.example.petstorespring.entity.Product;
import org.example.petstorespring.persistence.CategoryMapper;
import org.example.petstorespring.persistence.InventoryMapper;
import org.example.petstorespring.persistence.ItemMapper;
import org.example.petstorespring.persistence.ProductMapper;
import org.example.petstorespring.service.ManageService;
import org.example.petstorespring.vo.CategoryVO;
import org.example.petstorespring.vo.ItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("manageService")
public class ManageServiceImpl implements ManageService{
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.selectList(null); // 查询全部
    }

    @Override
    public List<Category> searchCategories(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCategories();
        }
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        // 假设你要模糊匹配 Name 或 CategoryId
        wrapper.like(Category::getName, keyword)
                .or()
                .like(Category::getCategoryId, keyword);
        return categoryMapper.selectList(wrapper);
    }

    @Override
    public boolean deleteCategorySafe(String categoryId) {
        // 1. 核心防御：查询 Product 表中是否还有该分类下的产品
        LambdaQueryWrapper<Product> productWrapper = new LambdaQueryWrapper<>();
        productWrapper.eq(Product::getCategoryId, categoryId);

        // 只要查到一条记录，就说明还有子数据，立刻拒绝删除
        if (productMapper.selectCount(productWrapper) > 0) {
            return false;
        }

        // 2. 安全通过，执行删除
        categoryMapper.deleteById(categoryId);
        return true;
    }

    @Override
    public Category getCategoryById(String categoryId) {
        return categoryMapper.selectById(categoryId);
    }

    @Override
    public void addCategory(Category category) {
        // 新增：直接插入
        categoryMapper.insert(category);
    }

    @Override
    public void updateCategory(Category category) {
        // 修改：根据主键 ID 更新其他字段
        categoryMapper.updateById(category);
    }

    @Override
    public List<Product> getAllProducts() {
        return productMapper.selectList(null);
    }

    @Override
    public List<Product> searchProducts(String keyword, String categoryId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        // 1. 如果选了具体的 Category，就加上这个条件
        if (categoryId != null && !categoryId.trim().isEmpty()) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }

        // 2. 如果填了关键字，就模糊匹配 Name 或 ProductId
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Product::getName, keyword)
                    .or()
                    .like(Product::getProductId, keyword));
        }

        return productMapper.selectList(wrapper);
    }

    @Override
    public boolean deleteProductSafe(String productId) {
        // 检查 Item 表里是否还有这个 productId 的数据
        LambdaQueryWrapper<Item> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(Item::getProductId, productId);

        if (itemMapper.selectCount(itemWrapper) > 0) {
            return false; // 还有子商品，拒绝删除！
        }

        productMapper.deleteById(productId);
        return true;
    }

    @Override
    public Product getProductById(String productId) {
        return productMapper.selectById(productId);
    }

    @Override
    public void addProduct(Product product) {
        productMapper.insert(product);
    }

    @Override
    public void updateProduct(Product product) {
        productMapper.updateById(product);
    }

    @Override
    public List<ItemVO> searchItems(String categoryId, String productId, String keyword) {
        LambdaQueryWrapper<Item> itemWrapper = new LambdaQueryWrapper<>();

        // 🎯 1. 处理下拉框的查询条件
        if (productId != null && !productId.trim().isEmpty()) {
            // 如果用户精确选择了某个产品（比如斑点狗），直接查这个产品下的 SKU
            itemWrapper.eq(Item::getProductId, productId);
        } else if (categoryId != null && !categoryId.trim().isEmpty()) {
            // 如果用户只选了分类（比如 DOGS），我们需要先查出 DOGS 下所有的产品 ID
            LambdaQueryWrapper<Product> prodWrapper = new LambdaQueryWrapper<>();
            prodWrapper.eq(Product::getCategoryId, categoryId);
            List<Product> products = productMapper.selectList(prodWrapper);

            if (products.isEmpty()) {
                // 这个分类下连产品都没有，肯定没有具体的 Item，直接返回空集合
                return new ArrayList<>();
            }

            // 把产品列表转换成 productId 的集合 (用了 Java 8 的 Stream API，非常优雅)
            List<String> productIds = products.stream().map(Product::getProductId).collect(Collectors.toList());

            // 告诉 Item 表：只要你的 productId 在这个集合里，就统统交出来！
            itemWrapper.in(Item::getProductId, productIds);
        }

        // 🎯 2. 处理关键字查询（模糊匹配 ItemID）
        if (keyword != null && !keyword.trim().isEmpty()) {
            itemWrapper.like(Item::getItemId, keyword);
        }

        // 🎯 3. 执行查询，拿到基础的 Item 列表
        List<Item> itemList = itemMapper.selectList(itemWrapper);

        // 🎯 4. 组装终极形态的 ItemVO 列表（跨表捞数据）
        List<ItemVO> itemVOList = new ArrayList<>();
        for (Item item : itemList) {
            ItemVO vo = new ItemVO();

            // 把 Item 里的基础数据（主键、价格、状态等）拷贝到 VO 里
            // 如果属性名一致，可以用 BeanUtils.copyProperties(item, vo); 偷懒
            vo.setItemId(item.getItemId());
            vo.setProductId(item.getProductId());
            vo.setListPrice(item.getListPrice());
            vo.setStatus(item.getStatus());

            // 🌟 跨表 1：去 Inventory 表查库存
            Inventory inventory = inventoryMapper.selectById(item.getItemId());
            if (inventory != null) {
                vo.setQuantity(inventory.getQuantity());
            } else {
                vo.setQuantity(0); // 防空指针，没记录就当 0 库存
            }

            // 🌟 跨表 2：去 Product 表查名字（为了页面展示更好看）
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                vo.setProductName(product.getName());
                vo.setCategoryId(product.getCategoryId());
            }

            itemVOList.add(vo);
        }

        return itemVOList;
    }

    @Override
    public void toggleItemStatus(String itemId, String currentStatus) {
        Item item = itemMapper.selectById(itemId);
        if (item != null) {
            // 简单的逻辑翻转
            String newStatus = "P".equals(currentStatus) ? "N" : "P";
            item.setStatus(newStatus);
            itemMapper.updateById(item);
        }
    }

    @Override
    public ItemVO getItemVOById(String itemId) {
        Item item = itemMapper.selectById(itemId);
        if (item == null) return null;

        ItemVO vo = new ItemVO();
        vo.setItemId(item.getItemId());
        vo.setProductId(item.getProductId());

        // 查库存
        Inventory inventory = inventoryMapper.selectById(itemId);
        vo.setQuantity(inventory != null ? inventory.getQuantity() : 0);

        return vo;
    }

    @Override
    public void updateStock(String itemId, Integer quantity) {
        // 先去数据库查一下有没有这个商品的库存记录
        Inventory inventory = inventoryMapper.selectById(itemId);

        if (inventory != null) {
            // 如果有，直接更新数量
            inventory.setQuantity(quantity);
            inventoryMapper.updateById(inventory);
        } else {
            // 🚨 防御性处理：如果连记录都没有，就新建一条插入进去
            Inventory newInventory = new Inventory();
            newInventory.setItemId(itemId);
            newInventory.setQuantity(quantity);
            inventoryMapper.insert(newInventory);
        }
    }

    @Override
    @Transactional // 🌟 极其重要：保证两张表要么同时成功，要么同时回滚报错
    public void addItem(Item item, Integer quantity ,int supplier) {
        // 1. 处理默认状态：如果没填，默认设为 "P" (上架)
        if (item.getStatus() == null || item.getStatus().isEmpty()) {
            item.setStatus("P");
        }

        item.setSupplier(supplier);

        // 2. 插入主表 Item
        itemMapper.insert(item);

        // 3. 插入子表 Inventory (库存)
        Inventory inventory = new Inventory();
        inventory.setItemId(item.getItemId());
        inventory.setQuantity(quantity != null ? quantity : 0);
        inventoryMapper.insert(inventory);
    }

    @Override
    @Transactional // 🌟 同样需要事务控制
    public void deleteItem(String itemId) {
        // 🚨 铁律：删数据必须先删子表，再删主表！
        // 1. 先删库存表
        inventoryMapper.deleteById(itemId);

        // 2. 再删商品表
        itemMapper.deleteById(itemId);
    }
}
