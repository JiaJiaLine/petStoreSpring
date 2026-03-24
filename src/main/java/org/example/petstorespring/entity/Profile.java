package org.example.petstorespring.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("profile")
@Data
public class Profile {
    @TableId("userid")
    private String username;
    @TableField("favcategory")
    private String favouriteCategoryId;
    @TableField("langpref")
    private String languagePreference;
    @TableField("mylistopt")
    private boolean listOption;
    @TableField("banneropt")
    private boolean bannerOption;
    //private String bannerName;
}
