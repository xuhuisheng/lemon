package com.mossle.user.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.auth.CustomPasswordEncoder;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserCache;
import com.mossle.api.user.UserDTO;

import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.user.persistence.domain.AccountAvatar;
import com.mossle.user.persistence.domain.AccountCredential;
import com.mossle.user.persistence.domain.AccountDept;
import com.mossle.user.persistence.domain.AccountDevice;
import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.domain.AccountLog;
import com.mossle.user.persistence.domain.AccountOnline;
import com.mossle.user.persistence.domain.PersonInfo;
import com.mossle.user.persistence.manager.AccountAvatarManager;
import com.mossle.user.persistence.manager.AccountCredentialManager;
import com.mossle.user.persistence.manager.AccountDeptManager;
import com.mossle.user.persistence.manager.AccountDeviceManager;
import com.mossle.user.persistence.manager.AccountInfoManager;
import com.mossle.user.persistence.manager.AccountLogManager;
import com.mossle.user.persistence.manager.AccountOnlineManager;
import com.mossle.user.persistence.manager.PersonInfoManager;
import com.mossle.user.publish.UserPublisher;
import com.mossle.user.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("user/info")
public class UserInfoController {
    private static Logger logger = LoggerFactory
            .getLogger(UserInfoController.class);
    private AccountInfoManager accountInfoManager;
    private AccountDeptManager accountDeptManager;
    private PersonInfoManager personInfoManager;
    private AccountCredentialManager accountCredentialManager;
    private AccountAvatarManager accountAvatarManager;
    private AccountDeviceManager accountDeviceManager;
    private AccountLogManager accountLogManager;
    private AccountOnlineManager accountOnlineManager;
    private UserCache userCache;
    private MessageHelper messageHelper;
    private BeanMapper beanMapper = new BeanMapper();
    private CustomPasswordEncoder customPasswordEncoder;
    private UserPublisher userPublisher;
    private TenantHolder tenantHolder;
    private UserService userService;

    /**
     * 部门人员列表.
     */
    @RequestMapping("index")
    public String index(
            @RequestParam(value = "deptId", required = false) Long deptId,
            Page page, Model model) {
        String path = null;

        if (deptId != null) {
            AccountDept accountDept = accountDeptManager.get(deptId);

            if (accountDept == null) {
                logger.info("cannot find dept : {}", deptId);
            } else {
                path = processDeptPath(accountDept);
            }
        }

        if (path != null) {
            String hql = "from PersonInfo where departmentPath like ?";
            page = personInfoManager.pagedQuery(hql, page.getPageNo(),
                    page.getPageSize(), path + "%");
        } else {
            String hql = "from PersonInfo ";
            page = personInfoManager.pagedQuery(hql, page.getPageNo(),
                    page.getPageSize());
        }

        model.addAttribute("page", page);

        return "user/info/index";
    }

    public String processDeptPath(AccountDept accountDept) {
        StringBuilder buff = new StringBuilder();

        while (accountDept != null) {
            accountDept = accountDeptManager.get(accountDept.getId());
            buff.insert(0, "/" + accountDept.getCode());
            accountDept = accountDept.getAccountDept();
        }

        return buff.toString();
    }

    /**
     * 搜索.
     */
    @RequestMapping("search")
    public String search(@RequestParam("username") List<String> username,
            Model model) {
        if ((username == null) || username.isEmpty()) {
            return "user/info/index";
        }

        String hql = "from PersonInfo where username in (:usernames)";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("usernames", username);

        Page page = personInfoManager.pagedQuery(hql, 1, 10, params);
        model.addAttribute("page", page);

        return "user/info/index";
    }

    /**
     * 详情，首页.
     */
    @RequestMapping("detail-index")
    public String index(@RequestParam("id") Long id, Model model) {
        PersonInfo personInfo = this.personInfoManager.get(id);

        if (personInfo == null) {
            logger.info("cannot find personInfo : {}", id);

            return "user/info/detail-index";
        }

        AccountInfo accountInfo = this.accountInfoManager.findUniqueBy("code",
                personInfo.getCode());

        if (accountInfo == null) {
            logger.info("cannot find accountInfo : {}", personInfo.getCode());

            return "user/info/detail-index";
        }

        model.addAttribute("accountInfo", accountInfo);
        model.addAttribute("personInfo", personInfo);

        return "user/info/detail-index";
    }

    /**
     * 详情，密码.
     */
    @RequestMapping("detail-password")
    public String password(@RequestParam("id") Long id, Model model) {
        PersonInfo personInfo = this.personInfoManager.get(id);

        if (personInfo == null) {
            logger.info("cannot find personInfo : {}", id);

            return "user/info/detail-password";
        }

        AccountInfo accountInfo = this.accountInfoManager.findUniqueBy("code",
                personInfo.getCode());

        if (accountInfo == null) {
            logger.info("cannot find accountInfo : {}", personInfo.getCode());

            return "user/info/detail-password";
        }

        Long infoId = accountInfo.getId();
        String hql = "from AccountCredential where accountInfo.id=?";
        List<AccountCredential> accountCredentials = this.accountCredentialManager
                .find(hql, infoId);

        model.addAttribute("accountCredentials", accountCredentials);

        model.addAttribute("accountInfo", accountInfo);

        return "user/info/detail-password";
    }

    /**
     * 详情，头像.
     */
    @RequestMapping("detail-avatar")
    public String avatar(@RequestParam("id") Long id, Model model) {
        PersonInfo personInfo = this.personInfoManager.get(id);

        if (personInfo == null) {
            logger.info("cannot find personInfo : {}", id);

            return "user/info/detail-avatar";
        }

        AccountInfo accountInfo = this.accountInfoManager.findUniqueBy("code",
                personInfo.getCode());

        if (accountInfo == null) {
            logger.info("cannot find accountInfo : {}", personInfo.getCode());

            return "user/info/detail-avatar";
        }

        Long infoId = accountInfo.getId();
        String hql = "from AccountAvatar where accountInfo.id=?";
        List<AccountAvatar> accountAvatars = this.accountAvatarManager.find(
                hql, infoId);

        model.addAttribute("accountAvatars", accountAvatars);

        model.addAttribute("accountInfo", accountInfo);

        return "user/info/detail-avatar";
    }

    /**
     * 详情，日志.
     */
    @RequestMapping("detail-log")
    public String log(@RequestParam("id") Long id, Model model) {
        PersonInfo personInfo = this.personInfoManager.get(id);

        if (personInfo == null) {
            logger.info("cannot find personInfo : {}", id);

            return "user/info/detail-log";
        }

        AccountInfo accountInfo = this.accountInfoManager.findUniqueBy("code",
                personInfo.getCode());

        if (accountInfo == null) {
            logger.info("cannot find accountInfo : {}", personInfo.getCode());

            return "user/info/detail-log";
        }

        Long infoId = accountInfo.getId();
        String hql = "from AccountLog where username=? order by id desc";
        List<AccountLog> accountLogs = this.accountLogManager.find(hql,
                accountInfo.getUsername());

        model.addAttribute("accountLogs", accountLogs);

        model.addAttribute("accountInfo", accountInfo);

        return "user/info/detail-log";
    }

    /**
     * 详情，设备.
     */
    @RequestMapping("detail-device")
    public String device(@RequestParam("id") Long id, Model model) {
        PersonInfo personInfo = this.personInfoManager.get(id);

        if (personInfo == null) {
            logger.info("cannot find personInfo : {}", id);

            return "user/info/detail-device";
        }

        AccountInfo accountInfo = this.accountInfoManager.findUniqueBy("code",
                personInfo.getCode());

        if (accountInfo == null) {
            logger.info("cannot find accountInfo : {}", personInfo.getCode());

            return "user/info/detail-device";
        }

        Long infoId = accountInfo.getId();
        String hql = "from AccountDevice where accountInfo.id=?";
        List<AccountDevice> accountDevices = this.accountDeviceManager.find(
                hql, infoId);

        model.addAttribute("accountDevices", accountDevices);

        model.addAttribute("accountInfo", accountInfo);

        return "user/info/detail-device";
    }

    /**
     * 详情，令牌.
     */
    @RequestMapping("detail-token")
    public String token(@RequestParam("id") Long id, Model model) {
        PersonInfo personInfo = this.personInfoManager.get(id);

        if (personInfo == null) {
            logger.info("cannot find personInfo : {}", id);

            return "user/info/detail-token";
        }

        AccountInfo accountInfo = this.accountInfoManager.findUniqueBy("code",
                personInfo.getCode());

        if (accountInfo == null) {
            logger.info("cannot find accountInfo : {}", personInfo.getCode());

            return "user/info/detail-token";
        }

        Long infoId = accountInfo.getId();
        String hql = "from AccountOnline where account=? order by id desc";
        List<AccountOnline> accountOnlines = this.accountOnlineManager.find(
                hql, Long.toString(accountInfo.getId()));

        model.addAttribute("accountOnlines", accountOnlines);

        model.addAttribute("accountInfo", accountInfo);

        return "user/info/detail-token";
    }

    /**
     * 详情，人员.
     */
    @RequestMapping("detail-person")
    public String person(@RequestParam("id") Long id, Model model) {
        PersonInfo personInfo = this.personInfoManager.get(id);

        if (personInfo == null) {
            logger.info("cannot find personInfo : {}", id);

            return "user/info/detail-person";
        }

        AccountInfo accountInfo = this.accountInfoManager.findUniqueBy("code",
                personInfo.getCode());

        if (accountInfo == null) {
            logger.info("cannot find accountInfo : {}", personInfo.getCode());

            return "user/info/detail-person";
        }

        model.addAttribute("accountInfo", accountInfo);
        model.addAttribute("personInfo", personInfo);

        return "user/info/detail-person";
    }

    /**
     * 新增.
     */
    @RequestMapping("input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        AccountInfo accountInfo = null;

        if (id != null) {
            accountInfo = accountInfoManager.get(id);
        } else {
            accountInfo = new AccountInfo();
        }

        model.addAttribute("model", accountInfo);

        return "user/info/input";
    }

    @RequestMapping("save")
    public String save(
            @ModelAttribute AccountInfo accountInfo,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
            RedirectAttributes redirectAttributes) throws Exception {
        String tenantId = tenantHolder.getTenantId();

        // 先进行校验
        if (password != null) {
            if (!password.equals(confirmPassword)) {
                messageHelper.addFlashMessage(redirectAttributes,
                        "user.user.input.passwordnotequals", "两次输入密码不符");

                // TODO: 还要填充schema
                return "user/info/detail-index";
            }
        }

        // 再进行数据复制
        AccountInfo dest = null;
        Long id = accountInfo.getId();

        if (id != null) {
            dest = accountInfoManager.get(id);

            if (accountInfo.getStatus() == null) {
                accountInfo.setStatus("disabled");
            }

            if (accountInfo.getLocked() == null) {
                accountInfo.setLocked("unlocked");
            }

            beanMapper.copy(accountInfo, dest);
        } else {
            dest = accountInfo;

            if (accountInfo.getStatus() == null) {
                accountInfo.setStatus("disabled");
            }

            if (accountInfo.getLocked() == null) {
                accountInfo.setLocked("unlocked");
            }

            dest.setCreateTime(new Date());
            dest.setTenantId(tenantId);
        }

        if (dest.getUsername() != null) {
            dest.setUsername(dest.getUsername().trim().toLowerCase());
        }

        accountInfoManager.save(dest);

        if (dest.getCode() == null) {
            dest.setCode(Long.toString(dest.getId()));
            accountInfoManager.save(dest);
        }

        if (password != null) {
            String hql = "from AccountCredential where accountInfo=? and catalog='default'";
            AccountCredential accountCredential = accountCredentialManager
                    .findUnique(hql, accountInfo);

            if (accountCredential == null) {
                accountCredential = new AccountCredential();
                accountCredential.setAccountInfo(accountInfo);
                accountCredential.setType("normal");
                accountCredential.setCatalog("default");
            }

            if (customPasswordEncoder != null) {
                accountCredential.setPassword(customPasswordEncoder
                        .encode(password));
            } else {
                accountCredential.setPassword(password);
            }

            accountCredentialManager.save(accountCredential);
        }

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        UserDTO userDto = new UserDTO();
        userDto.setId(Long.toString(dest.getId()));
        userDto.setUsername(dest.getUsername());
        userDto.setRef(dest.getCode());
        userDto.setUserRepoRef(tenantId);
        userCache.removeUser(userDto);

        if (id != null) {
            userPublisher.notifyUserUpdated(this.convertUserDto(dest));
        } else {
            userPublisher.notifyUserCreated(this.convertUserDto(dest));
        }

        PersonInfo personInfo = personInfoManager.findUniqueBy("code",
                dest.getCode());

        if (personInfo == null) {
            personInfo = new PersonInfo();
            personInfo.setCode(accountInfo.getCode());
            personInfo.setUsername(accountInfo.getUsername());
            personInfo.setDisplayName(accountInfo.getDisplayName());
            personInfo.setType(accountInfo.getType());
            personInfo.setStatus(accountInfo.getStatus());
            personInfoManager.save(personInfo);
        }

        return "redirect:/user/info/detail-index.do?id=" + personInfo.getId();
    }

    /**
     * 批量编辑.
     */
    @RequestMapping("batch-input")
    public String input(@RequestParam("selectedItem") List<Long> selectedItem,
            Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PersonInfo> personInfos = personInfoManager
                .findByIds(selectedItem);
        // logger.info("{}", selectedItem);
        model.addAttribute("personInfos", personInfos);

        return "user/info/batch-input";
    }

    /**
     * 批量保存.
     */
    @RequestMapping("batch-save")
    public String save(@RequestParam("selectedItem") List<Long> selectedItem,
            @RequestParam("type") List<String> types,
            @RequestParam("value") List<String> values) {
        List<PersonInfo> personInfos = personInfoManager
                .findByIds(selectedItem);
        List<String> usernames = new ArrayList<String>();

        for (PersonInfo personInfo : personInfos) {
            usernames.add(personInfo.getUsername());
        }

        for (int i = 0; i < types.size(); i++) {
            String type = types.get(i);
            String value = values.get(i);

            if ("status".equals(type)) {
                for (PersonInfo personInfo : personInfos) {
                    personInfo.setStatus(value);
                    personInfoManager.save(personInfo);
                }
            } else if ("type".equals(type)) {
                for (PersonInfo personInfo : personInfos) {
                    personInfo.setType(value);
                    personInfoManager.save(personInfo);
                }
            }
        }

        StringBuilder buff = new StringBuilder();

        for (String username : usernames) {
            buff.append("&username=").append(username);
        }

        return "redirect:/user/info/search.do?" + buff.toString();
    }

    /**
     * 删除.
     */
    @RequestMapping("remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        List<PersonInfo> personInfos = personInfoManager
                .findByIds(selectedItem);

        for (PersonInfo personInfo : personInfos) {
            AccountInfo accountInfo = this.userService.removePerson(personInfo
                    .getId());

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

        return "redirect:/user/info/index.do";
    }

    @RequestMapping("generate-password")
    public String generatePassword(@RequestParam("id") Long id) {
        AccountCredential accountCredential = this.accountCredentialManager
                .get(id);
        AccountInfo accountInfo = accountCredential.getAccountInfo();
        PersonInfo personInfo = personInfoManager.findUniqueBy("code",
                accountInfo.getCode());

        String password = userService.generatePassword(id);

        return "redirect:/user/info/detail-password.do?id="
                + personInfo.getId() + "&credentialId="
                + accountCredential.getId() + "&password=" + password;
    }

    @RequestMapping("person-info-save")
    public String personInfoSave(@ModelAttribute PersonInfo personInfo,
            RedirectAttributes redirectAttributes) throws Exception {
        // 再进行数据复制
        PersonInfo dest = null;
        Long id = personInfo.getId();

        if (id != null) {
            dest = personInfoManager.get(id);
            beanMapper.copy(personInfo, dest);
        } else {
            dest = personInfo;
        }

        personInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        AccountInfo accountInfo = accountInfoManager.findUniqueBy("code",
                personInfo.getCode());

        if (accountInfo != null) {
            UserDTO userDto = new UserDTO();
            userDto.setId(Long.toString(accountInfo.getId()));
            userDto.setUsername(accountInfo.getUsername());
            userDto.setRef(accountInfo.getCode());
            userDto.setUserRepoRef("1");
            userCache.removeUser(userDto);
        }

        return "redirect:/user/info/detail-person.do?id=" + personInfo.getId();
    }

    public UserDTO convertUserDto(AccountInfo accountInfo) {
        String hql = "from PersonInfo where code=? and tenantId=?";
        PersonInfo personInfo = personInfoManager.findUnique(hql,
                accountInfo.getCode(), accountInfo.getTenantId());

        UserDTO userDto = new UserDTO();
        userDto.setId(Long.toString(accountInfo.getId()));
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
    public void setAccountDeptManager(AccountDeptManager accountDeptManager) {
        this.accountDeptManager = accountDeptManager;
    }

    @Resource
    public void setPersonInfoManager(PersonInfoManager personInfoManager) {
        this.personInfoManager = personInfoManager;
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
    public void setAccountDeviceManager(
            AccountDeviceManager accountDeviceManager) {
        this.accountDeviceManager = accountDeviceManager;
    }

    @Resource
    public void setAccountLogManager(AccountLogManager accountLogManager) {
        this.accountLogManager = accountLogManager;
    }

    @Resource
    public void setAccountOnlineManager(
            AccountOnlineManager accountOnlineManager) {
        this.accountOnlineManager = accountOnlineManager;
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
    public void setUserPublisher(UserPublisher userPublisher) {
        this.userPublisher = userPublisher;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
