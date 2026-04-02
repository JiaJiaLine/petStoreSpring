package org.example.petstorespring.service;


import jakarta.servlet.http.HttpSession;
import org.example.petstorespring.entity.Account;
import org.example.petstorespring.vo.LoginAccountVO;
import org.example.petstorespring.vo.SignOnVO;

import java.util.List;

public interface AccountService {
    SignOnVO getSignOn(String username, String password);
    Account getAccount(String username);
    Account getAccount();
    LoginAccountVO getLoginAccount();
    void updateAccount(LoginAccountVO loginAccountVO, HttpSession session);
    void setAccount(Account newaccount);
    void generateCaptcha(String captcha, HttpSession session);
    List<Account> getAllAccounts();
}
