package com.mossle.user.web;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.auth.CustomPasswordEncoder;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserCache;
import com.mossle.api.user.UserDTO;

import com.mossle.core.export.Exportor;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.user.persistence.domain.AccountAvatar;
import com.mossle.user.persistence.domain.AccountCredential;
import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.domain.PersonInfo;
import com.mossle.user.persistence.manager.AccountAvatarManager;
import com.mossle.user.persistence.manager.AccountCredentialManager;
import com.mossle.user.persistence.manager.AccountInfoManager;
import com.mossle.user.persistence.manager.PersonInfoManager;
import com.mossle.user.publish.UserPublisher;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("user")
public class AccountCredentialController {
    private AccountInfoManager accountInfoManager;
    private AccountCredentialManager accountCredentialManager;
    private AccountAvatarManager accountAvatarManager;
    private PersonInfoManager personInfoManager;
    private UserCache userCache;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private CustomPasswordEncoder customPasswordEncoder;
    private UserPublisher userPublisher;
    private TenantHolder tenantHolder;

    @RequestMapping("account-credential-generate")
    public String generate(@RequestParam("id") Long id) {
        AccountCredential accountCredential = this.accountCredentialManager
                .get(id);
        String password = this.generatePassword();
        accountCredential.setPassword(customPasswordEncoder.encode(password));

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 90);
        accountCredential.setExpireTime(calendar.getTime());
        accountCredentialManager.save(accountCredential);

        return "redirect:/user/account-detail-password.do?id=" + id
                + "&infoId=" + accountCredential.getAccountInfo().getId()
                + "&password=" + password;
    }

    public String generatePassword() {
        String[] pa = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
                "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w",
                "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I",
                "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
                "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6",
                "7", "8", "9" };
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < 8; i++) {
            sb.append(pa[(Double.valueOf(Math.random() * pa.length).intValue())]);
        }

        String[] spe = { "`", "~", "!", "@", "#", "$", "%", "^", "&", "*", "(",
                ")", "-", "_", "=", "+", "[", "]", "{", "}", "\\", "/", "?",
                ",", ".", "<", ">" };
        sb.append(spe[(Double.valueOf(Math.random() * spe.length).intValue())]);
        sb.append((int) (Math.random() * 100));

        return sb.toString();
    }

    // ~ ======================================================================
    @Resource
    public void setAccountInfoManager(AccountInfoManager accountInfoManager) {
        this.accountInfoManager = accountInfoManager;
    }

    @Resource
    public void setAccountCredentialManager(
            AccountCredentialManager accountCredentialManager) {
        this.accountCredentialManager = accountCredentialManager;
    }

    @Resource
    public void setAccountAvatarManager(
            AccountAvatarManager accountAvatarManager) {
        this.accountAvatarManager = accountAvatarManager;
    }

    @Resource
    public void setPersonInfoManager(PersonInfoManager personInfoManager) {
        this.personInfoManager = personInfoManager;
    }

    @Resource
    public void setUserCache(UserCache userCache) {
        this.userCache = userCache;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setCustomPasswordEncoder(
            CustomPasswordEncoder customPasswordEncoder) {
        this.customPasswordEncoder = customPasswordEncoder;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setUserPublisher(UserPublisher userPublisher) {
        this.userPublisher = userPublisher;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
