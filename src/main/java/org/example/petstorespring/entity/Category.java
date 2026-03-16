package org.example.petstorespring.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("category")
public class Category {
    @TableId("catid")
    private String categoryId;
    private String name;
    @TableField("descn")
    private String description;
}
