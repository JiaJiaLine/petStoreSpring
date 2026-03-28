package org.example.petstorespring.service.impl;


import jakarta.servlet.http.HttpSession;
import org.example.petstorespring.entity.Account;
import org.example.petstorespring.entity.BannerData;
import org.example.petstorespring.entity.Profile;
import org.example.petstorespring.entity.SignOn;
import org.example.petstorespring.persistence.AccountMapper;
import org.example.petstorespring.persistence.BannerDataMapper;
import org.example.petstorespring.persistence.ProfileMapper;
import org.example.petstorespring.persistence.SignOnMapper;
import org.example.petstorespring.service.AccountService;
import org.example.petstorespring.vo.LoginAccountVO;
import org.example.petstorespring.vo.SignOnVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("accountService")
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private SignOnMapper signOnMapper;
    @Autowired
    private BannerDataMapper bannerDataMapper;
    @Autowired
    private ProfileMapper profileMapper;


    private Account account;
    //private SignOn signOn;

    @Override
    public SignOnVO getSignOn(String username, String password) {

        SignOn signOn=signOnMapper.selectById(username);
        SignOnVO signOnVO=new SignOnVO();
        if(username==null||password==null){
            signOnVO.setSignOnMsg("请输入账号密码");
            return signOnVO;
        }
        if(signOn==null){
            signOnVO.setSignOnMsg("用户不存在");
         return signOnVO;
        }
        if(!password.equals(signOn.getPassword())){
            signOnVO.setSignOnMsg("密码错误");
            return signOnVO;
        }
        signOnVO.setSignOnMsg(null);
        signOn.setUsername(username);
        signOn.setPassword(password);
        return signOnVO;
    }

    @Override
    public Account getAccount(String username) {
         account=accountMapper.selectById(username);
        return account;
    }

    @Override
    public Account getAccount() {

        return account;
    }

    @Override
    public void setAccount(Account newaccount){
        account=newaccount;
}

    //哈哈哈
    @Override
    public void generateCaptcha(String captcha, HttpSession session) {
        session.setAttribute("captchaCode", captcha);
    }

    @Override
    public LoginAccountVO getLoginAccount() {
        LoginAccountVO loginAccountVO = new LoginAccountVO();

        //account为空时，返回空VO
        if (account == null) {
            return loginAccountVO; // 所有字段默认null
        }

        String username = account.getUsername();
        if (username == null || username.isEmpty()) {
            return loginAccountVO;
        }

        // 查询关联数据（保留空值，不强制赋值）
        Profile profile = null;
        BannerData bannerData = null;
        try {
            profile = profileMapper.selectById(username);
            bannerData = bannerDataMapper.selectById(profile.getFavouriteCategoryId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //赋值：非密码字段直接透传null，仅布尔值赋默认
        loginAccountVO.setUsername(account.getUsername()); // 可空
        loginAccountVO.setAddress1(account.getAddress1()); // 可空
        loginAccountVO.setCity(account.getCity()); // 可空
        loginAccountVO.setCountry(account.getCountry()); // 可空
        loginAccountVO.setEmail(account.getEmail()); // 可空
        loginAccountVO.setAddress2(account.getAddress2()); // 可空
        loginAccountVO.setFirstName(account.getFirstName()); // 可空
        loginAccountVO.setZip(account.getZip()); // 可空
        loginAccountVO.setStatus(account.getStatus()); // 可空
        loginAccountVO.setLastName(account.getLastName()); // 可空
        loginAccountVO.setState(account.getState());
        loginAccountVO.setPhone(account.getPhone());
        loginAccountVO.setManager(account.getIsManager());

        // Profile
        if (profile != null) {
            loginAccountVO.setBannerOption(profile.isBannerOption());
            loginAccountVO.setListOption(profile.isListOption());
            loginAccountVO.setFavouriteCategoryId(profile.getFavouriteCategoryId());
            loginAccountVO.setLanguagePreference(profile.getLanguagePreference());
        } else {
            loginAccountVO.setBannerOption(false);
            loginAccountVO.setListOption(false);
            loginAccountVO.setFavouriteCategoryId(null);
            loginAccountVO.setLanguagePreference(null);
        }

        // BannerData
        if (bannerData != null) {
            loginAccountVO.setBannerName(bannerData.getBannerName());
        } else {
            loginAccountVO.setBannerName(null);
        }


         loginAccountVO.setPassword(null);

        return loginAccountVO;
    }

    @Override
    @Transactional
    public void updateAccount(LoginAccountVO loginAccountVO, HttpSession session) {
        // 1. 统一获取用户名（注册：从VO取；更新：从Session已登录account取）
        String username;
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount != null) {
            username = sessionAccount.getUsername();
        } else {
            username = loginAccountVO.getUsername();
            if (username == null || username.isEmpty()) {
                loginAccountVO.setMsg("用户名不能为空");
                return;
            }
            if (accountMapper.selectById(username) != null) {
                loginAccountVO.setMsg("用户名已存在，无法注册");
                return;
            }
        }

        // 2. 处理 Account 表（新增/更新自动判断）
        Account account1 = accountMapper.selectById(username);
        boolean isNewAccount = (account1 == null);
        if (isNewAccount) {
            // 注册场景：新建Account对象
            account1 = new Account();
            account1.setStatus("active"); // 注册默认激活状态
        }

        BeanUtils.copyProperties(loginAccountVO, account1);
        account1.setUsername(username); // 确保主键不丢
        // 执行新增/更新
        if (isNewAccount) {
            accountMapper.insert(account1);
        } else {
            accountMapper.updateById(account1);
        }

        // 3. 处理Profile
        Profile profile = profileMapper.selectById(username);
        boolean isNewProfile = (profile == null);
        if (isNewProfile) {
            profile = new Profile();
        }
        BeanUtils.copyProperties(loginAccountVO, profile);
        profile.setUsername(username); // 绑定主键
        // 执行新增/更新
        if (isNewProfile) {
            profileMapper.insert(profile);
        } else {
            profileMapper.updateById(profile);
        }
        SignOn signOn=signOnMapper.selectById(username);
        boolean isNewSignOn=(signOn==null);
        if(isNewSignOn){
            signOn=new SignOn();
        }
      BeanUtils.copyProperties(loginAccountVO,signOn);
        signOn.setUsername(username);
        if(isNewSignOn){
            signOnMapper.insert(signOn);
        }else{
            signOnMapper.updateById(signOn);

        }

        // 4. 注册成功后，自动存入Session（模拟登录态）
        if (isNewAccount) {
            session.setAttribute("account", account1);
        }
    }}