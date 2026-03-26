package org.example.petstorespring.service;


import jakarta.servlet.http.HttpSession;
import org.example.petstorespring.entity.Account;
import org.example.petstorespring.vo.LoginAccountVO;
import org.example.petstorespring.vo.SignOnVO;

public interface AccountService {
    SignOnVO getSignOn(String username, String password);
    Account getAccount(String username);
    Account getAccount();
    LoginAccountVO getLoginAccount();
    void updateAccount(LoginAccountVO loginAccountVO, HttpSession session);

}
