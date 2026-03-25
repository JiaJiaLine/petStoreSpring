package org.example.petstorespring.controller;


import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.example.petstorespring.entity.Account;
import org.example.petstorespring.service.AccountService;
import org.example.petstorespring.vo.LoginAccountVO;
import org.example.petstorespring.vo.SignOnVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Controller
//@RequestMapping("/account")
public class AccountController {
@Autowired
    private AccountService accountService;
   @GetMapping("/account/viewSignon")
    public String signOn(Model model){
       // SignOnVO signOnVO=accountService.getSignOn(username,password);
       SignOnVO emptySignOnVO = new SignOnVO();
       emptySignOnVO.setSignOnMsg(null); // 显式清空错误信息
       model.addAttribute("signOn", emptySignOnVO);

        return "account/signon";
    }
    @PostMapping("/account/signOn")
    public String handleSignOnSubmit(
            String username,  // 接收表单 username 参数
            String password,  // 接收表单 password 参数
            String captcha,   // 可选：接收验证码参数（如果表单有）
            Model model,
            HttpSession session) {

        SignOnVO signOnVO = accountService.getSignOn(username, password);
        model.addAttribute("signOn", signOnVO);
       session.setAttribute("account",accountService.getAccount(username));
       session.setAttribute("authenticated",true);
        LoginAccountVO loginAccountVO=accountService.getLoginAccount();
        session.setAttribute("loginAccount",loginAccountVO);

        if (signOnVO.getSignOnMsg() == null) {
            return "catalog/main";
        } else {
            return "account/signon";
        }

    }

    @GetMapping("/signoff")
    public String signOff(HttpSession session){
       session.removeAttribute("account");
       session.removeAttribute("loginAccount");
       session.removeAttribute("authenticated");
       accountService.setAccount(null);
       return"../static/index";
    }

    @GetMapping("/account/editAccountForm")
    public String getEdit(HttpSession session,Model model){

        List<String> languageList = Arrays.asList("zh-CN", "en-US", "ja-JP");
        List<String> favList = Arrays.asList("BIRDS",
                "CATS",
                "DOGS",
                "FISH",
                "REPTILES"
        );
        model.addAttribute("favouriteCategoryId",favList);
        model.addAttribute("languagePreference",languageList);
        LoginAccountVO loginAccountVO=accountService.getLoginAccount();
        model.addAttribute("loginAccount",loginAccountVO);
        session.setAttribute("loginAccount",loginAccountVO);
        return "account/editAccount";
   }

    @PostMapping("/account/editAccount")
    public String postEdit(  LoginAccountVO loginAccountVO,HttpSession session){
      accountService.updateAccount(loginAccountVO,session);
if(loginAccountVO.getMsg()!=null)
{

    session.setAttribute("loginAccount",loginAccountVO);
    return"account/editAccount";
}
    else{    Account updatedAccount = accountService.getAccount(loginAccountVO.getUsername());
        session.setAttribute("account", updatedAccount); // 更新用户信息
        session.setAttribute("authenticated",true);
        session.setAttribute("loginAccount",loginAccountVO);
        return "catalog/main";}
    }
}
