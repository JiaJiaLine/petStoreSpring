package org.example.petstorespring.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class LoginAccountVO {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String status;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zip;
    private String country;
    private String phone;
    private String favouriteCategoryId;
    private String languagePreference;
    private boolean listOption;
    private boolean bannerOption;
    private String bannerName;
    private String Msg;
    private String repeatPassword;
    private boolean isManager;
}
