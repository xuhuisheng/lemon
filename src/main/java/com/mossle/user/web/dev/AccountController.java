package com.mossle.user.web.dev;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.auth.CustomPasswordEncoder;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserCache;
import com.mossle.api.user.UserConnector;
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

import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("user/dev")
public class AccountController {
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
    private UserConnector userConnector;

    @RequestMapping("list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = accountInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "user/dev/list";
    }

    @RequestMapping("remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        List<AccountInfo> accountInfos = accountInfoManager
                .findByIds(selectedItem);

        for (AccountInfo accountInfo : accountInfos) {
            for (AccountCredential accountCredential : accountInfo
                    .getAccountCredentials()) {
                accountCredentialManager.remove(accountCredential);
            }

            for (AccountAvatar accountAvatar : accountInfo.getAccountAvatars()) {
                accountAvatarManager.remove(accountAvatar);
            }

            accountInfoManager.remove(accountInfo);

            UserDTO userDto = new UserDTO();
            userDto.setId(Long.toString(accountInfo.getId()));
            userDto.setUsername(accountInfo.getUsername());
            userDto.setRef(accountInfo.getCode());
            userDto.setUserRepoRef(tenantId);
            userCache.removeUser(userDto);
            userPublisher.notifyUserRemoved(this.convertUserDto(accountInfo));
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/user/account-info-list.do";
    }

    @RequestMapping("account-info-checkUsername")
    @ResponseBody
    public boolean checkUsername(@RequestParam("username") String username,
            @RequestParam(value = "id", required = false) Long id)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        String hql = "from AccountInfo where username=? and tenantId=?";
        Object[] params = { username, tenantId };

        if (id != null) {
            hql = "from AccountInfo where username=? and tenantId=? and id<>?";
            params = new Object[] { username, tenantId, id };
        }

        boolean result = accountInfoManager.findUnique(hql, params) == null;

        return result;
    }

    @RequestMapping("import-view")
    public String importView() throws Exception {
        return "user/dev/import-view";
    }

    @RequestMapping("import-save")
    public String importSave(@RequestParam("text") String text, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();

        for (String line : text.split("\n")) {
            if (StringUtils.isBlank(line)) {
                continue;
            }

            String[] array = line.split("\t");
            String username = array[0];
            String displayName = array[1];
            String mobile = array[2];
            String email = array[3];

            Map<String, String> map = new HashMap<String, String>();
            map.put("username", username);
            map.put("displayName", displayName);
            map.put("mobile", mobile);
            map.put("email", email);
            list.add(map);

            AccountInfo accountInfo = accountInfoManager.findUnique(
                    "from AccountInfo where username=? and tenantId=?",
                    username, tenantId);

            if (accountInfo != null) {
                map.put("result", "ERROR : username exists");

                continue;
            }

            PersonInfo personInfo = null;
            personInfo = personInfoManager.findUnique(
                    "from PersonInfo where cellphone=? and tenantId=?", mobile,
                    tenantId);

            if (personInfo != null) {
                map.put("result", "ERROR : mobile exists");

                continue;
            }

            personInfo = personInfoManager.findUnique(
                    "from PersonInfo where email=? and tenantId=?", email,
                    tenantId);

            if (personInfo != null) {
                map.put("result", "ERROR : email exists");

                continue;
            }

            accountInfo = new AccountInfo();
            accountInfo.setUsername(username);
            accountInfo.setDisplayName(displayName);
            accountInfo.setStatus("active");
            accountInfo.setCreateTime(new Date());
            accountInfo.setTenantId(tenantId);
            accountInfoManager.save(accountInfo);
            accountInfo.setCode(Long.toString(accountInfo.getId()));
            accountInfoManager.save(accountInfo);

            personInfo = new PersonInfo();
            personInfo.setCode(accountInfo.getCode());
            personInfo.setUsername(username);
            personInfo.setCellphone(mobile);
            personInfo.setEmail(email);
            personInfo.setTenantId(tenantId);
            personInfoManager.save(personInfo);
            map.put("result", "SUCCESS");
        }

        model.addAttribute("list", list);

        return "user/dev/import-result";
    }

    @RequestMapping("export-view")
    public String exportView(Model model) throws Exception {
        List<AccountInfo> accountInfos = this.accountInfoManager.getAll("id",
                true);
        List<UserDTO> userDtos = new ArrayList<UserDTO>();

        for (AccountInfo accountInfo : accountInfos) {
            UserDTO userDto = userConnector.findById(accountInfo.getCode());
            userDtos.add(userDto);
        }

        model.addAttribute("userDtos", userDtos);

        return "user/dev/export-view";
    }

    //
    public UserDTO convertUserDto(AccountInfo accountInfo) {
        String hql = "from PersonInfo where code=? and tenantId=?";
        PersonInfo personInfo = personInfoManager.findUnique(hql,
                accountInfo.getCode(), accountInfo.getTenantId());

        UserDTO userDto = new UserDTO();
        userDto.setId(accountInfo.getCode());
        userDto.setUsername(accountInfo.getUsername());
        userDto.setDisplayName(accountInfo.getDisplayName());
        userDto.setNickName(accountInfo.getNickName());
        userDto.setUserRepoRef(accountInfo.getTenantId());

        if (personInfo != null) {
            userDto.setEmail(personInfo.getEmail());
            userDto.setMobile(personInfo.getCellphone());
        }

        return userDto;
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

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }
}
