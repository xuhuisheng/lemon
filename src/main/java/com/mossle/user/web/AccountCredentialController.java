package com.mossle.user.web;

import java.util.Calendar;

import javax.annotation.Resource;

import com.mossle.api.auth.CustomPasswordEncoder;

import com.mossle.user.persistence.domain.AccountCredential;
import com.mossle.user.persistence.manager.AccountCredentialManager;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("user")
public class AccountCredentialController {
    private AccountCredentialManager accountCredentialManager;
    private CustomPasswordEncoder customPasswordEncoder;

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
    public void setAccountCredentialManager(
            AccountCredentialManager accountCredentialManager) {
        this.accountCredentialManager = accountCredentialManager;
    }
}
