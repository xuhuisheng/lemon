package com.mossle.user.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.auth.CustomPasswordEncoder;
import com.mossle.api.user.UserDTO;

import com.mossle.user.persistence.domain.AccountAvatar;
import com.mossle.user.persistence.domain.AccountCredential;
import com.mossle.user.persistence.domain.AccountDevice;
import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.domain.PersonInfo;
import com.mossle.user.persistence.domain.UserAttr;
import com.mossle.user.persistence.domain.UserBase;
import com.mossle.user.persistence.domain.UserSchema;
import com.mossle.user.persistence.manager.AccountAvatarManager;
import com.mossle.user.persistence.manager.AccountCredentialManager;
import com.mossle.user.persistence.manager.AccountDeviceManager;
import com.mossle.user.persistence.manager.AccountInfoManager;
import com.mossle.user.persistence.manager.PersonInfoManager;
import com.mossle.user.persistence.manager.UserAttrManager;
import com.mossle.user.persistence.manager.UserBaseManager;
import com.mossle.user.persistence.manager.UserRepoManager;
import com.mossle.user.persistence.manager.UserSchemaManager;
import com.mossle.user.publish.UserPublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserService {
    private static Logger logger = LoggerFactory.getLogger(UserService.class);
    private UserBaseManager userBaseManager;
    private UserRepoManager userRepoManager;
    private UserAttrManager userAttrManager;
    private UserSchemaManager userSchemaManager;
    private UserPublisher userPublisher;
    private AccountInfoManager accountInfoManager;
    private PersonInfoManager personInfoManager;
    private AccountCredentialManager accountCredentialManager;
    private AccountAvatarManager accountAvatarManager;
    private AccountDeviceManager accountDeviceManager;
    private CustomPasswordEncoder customPasswordEncoder;

    /**
     * 添加用户.
     * 
     * @param userBase
     *            UserBase
     * @param userRepoId
     *            Long
     * @param parameters
     *            Map
     */
    public void insertUser(UserBase userBase, Long userRepoId,
            Map<String, Object> parameters) {
        // user repo
        userBase.setUserRepo(userRepoManager.get(userRepoId));

        // userBase.setTenantId(TenantHolder.getTenantId());
        userBaseManager.save(userBase);

        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            String key = entry.getKey();
            String value = (String) entry.getValue();

            UserSchema userSchema = userSchemaManager.findUnique(
                    "from UserSchema where code=? and userRepo.id=?", key,
                    userRepoId);

            if (userSchema == null) {
                logger.debug("skip : {}", key);

                continue;
            }

            UserAttr userAttr = new UserAttr();
            userAttr.setUserSchema(userSchema);
            userAttr.setUserBase(userBase);
            // userAttr.setTenantId(TenantHolder.getTenantId());
            userAttrManager.save(userAttr);

            String type = userSchema.getType();

            if ("boolean".equals(type)) {
                userAttr.setBooleanValue(Integer.parseInt(value));
            } else if ("date".equals(type)) {
                try {
                    userAttr.setDateValue(new SimpleDateFormat("yyyy-MM-dd")
                            .parse(value));
                } catch (ParseException ex) {
                    logger.info(ex.getMessage(), ex);
                }
            } else if ("long".equals(type)) {
                userAttr.setLongValue(Long.parseLong(value));
            } else if ("double".equals(type)) {
                userAttr.setDoubleValue(Double.parseDouble(value));
            } else if ("string".equals(type)) {
                userAttr.setStringValue(value);
            } else {
                throw new IllegalStateException("illegal type: "
                        + userSchema.getType());
            }

            userAttrManager.save(userAttr);
        }

        userPublisher.notifyUserCreated(this.convertUserDto(userBase));
    }

    /**
     * 更新用户.
     * 
     * @param userBase
     *            UserBase
     * @param userRepoId
     *            Long
     * @param parameters
     *            Map
     */
    public void updateUser(UserBase userBase, Long userRepoId,
            Map<String, Object> parameters) {
        // user repo
        userBase.setUserRepo(userRepoManager.get(userRepoId));
        userBaseManager.save(userBase);

        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            String key = entry.getKey();
            String value = this.getStringValue(entry.getValue());

            UserSchema userSchema = userSchemaManager.findUnique(
                    "from UserSchema where code=? and userRepo.id=?", key,
                    userRepoId);

            if (userSchema == null) {
                logger.debug("skip : {}", key);

                continue;
            }

            UserAttr userAttr = userAttrManager.findUnique(
                    "from UserAttr where userSchema=? and userBase=?",
                    userSchema, userBase);

            if (userAttr == null) {
                userAttr = new UserAttr();
                userAttr.setUserSchema(userSchema);
                userAttr.setUserBase(userBase);

                // userAttr.setTenantId(TenantHolder.getTenantId());
            }

            String type = userSchema.getType();

            if ("boolean".equals(type)) {
                userAttr.setBooleanValue(Integer.parseInt(value));
            } else if ("date".equals(type)) {
                try {
                    userAttr.setDateValue(new SimpleDateFormat("yyyy-MM-dd")
                            .parse(value));
                } catch (ParseException ex) {
                    logger.info(ex.getMessage(), ex);
                }
            } else if ("long".equals(type)) {
                userAttr.setLongValue(Long.parseLong(value));
            } else if ("double".equals(type)) {
                userAttr.setDoubleValue(Double.parseDouble(value));
            } else if ("string".equals(type)) {
                userAttr.setStringValue(value);
            } else {
                throw new IllegalStateException("illegal type: "
                        + userSchema.getType());
            }

            userAttrManager.save(userAttr);
        }

        userPublisher.notifyUserUpdated(this.convertUserDto(userBase));
    }

    /**
     * 删除用户.
     * 
     * @param userBase
     *            UserBase
     */
    public void removeUser(UserBase userBase) {
        userBaseManager.removeAll(userBase.getUserAttrs());
        userBaseManager.remove(userBase);
        userPublisher.notifyUserRemoved(this.convertUserDto(userBase));
    }

    public AccountInfo removeAccount(long accountId) {
        AccountInfo accountInfo = accountInfoManager.get(accountId);

        if (accountInfo == null) {
            logger.info("cannot find account : {}", accountId);

            return null;
        }

        this.removeAccountInternal(accountInfo);

        PersonInfo personInfo = personInfoManager.findUniqueBy("code",
                accountInfo.getCode());

        if (personInfo != null) {
            personInfoManager.remove(personInfo);
        }

        return accountInfo;
    }

    public AccountInfo removePerson(long personId) {
        PersonInfo personInfo = personInfoManager.get(personId);

        if (personInfo == null) {
            logger.info("cannot find person : {}", personId);

            return null;
        }

        AccountInfo accountInfo = accountInfoManager.findUniqueBy("code",
                personInfo.getCode());
        this.removeAccountInternal(accountInfo);
        personInfoManager.remove(personInfo);

        return accountInfo;
    }

    public void removeAccountInternal(AccountInfo accountInfo) {
        for (AccountCredential accountCredential : accountInfo
                .getAccountCredentials()) {
            accountCredentialManager.remove(accountCredential);
        }

        for (AccountAvatar accountAvatar : accountInfo.getAccountAvatars()) {
            accountAvatarManager.remove(accountAvatar);
        }

        for (AccountDevice accountDevice : accountInfo.getAccountDevices()) {
            accountDeviceManager.remove(accountDevice);
        }

        accountInfoManager.remove(accountInfo);
    }

    public String generatePassword(Long credentialId) {
        AccountCredential accountCredential = this.accountCredentialManager
                .get(credentialId);
        String password = this.generatePassword();
        accountCredential.setPassword(customPasswordEncoder.encode(password));

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 90);
        accountCredential.setExpireTime(calendar.getTime());
        accountCredentialManager.save(accountCredential);

        return password;
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

    // ~
    public String getStringValue(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            return (String) value;
        }

        return value.toString();
    }

    public UserDTO convertUserDto(UserBase userBase) {
        UserDTO userDto = new UserDTO();
        userDto.setId(Long.toString(userBase.getId()));
        userDto.setUsername(userBase.getUsername());
        userDto.setDisplayName(userBase.getNickName());
        userDto.setEmail(userBase.getEmail());
        userDto.setMobile(userBase.getMobile());

        return userDto;
    }

    @Resource
    public void setUserBaseManager(UserBaseManager userBaseManager) {
        this.userBaseManager = userBaseManager;
    }

    @Resource
    public void setUserRepoManager(UserRepoManager userRepoManager) {
        this.userRepoManager = userRepoManager;
    }

    @Resource
    public void setUserAttrManager(UserAttrManager userAttrManager) {
        this.userAttrManager = userAttrManager;
    }

    @Resource
    public void setUserSchemaManager(UserSchemaManager userSchemaManager) {
        this.userSchemaManager = userSchemaManager;
    }

    @Resource
    public void setUserPublisher(UserPublisher userPublisher) {
        this.userPublisher = userPublisher;
    }

    @Resource
    public void setAccountInfoManager(AccountInfoManager accountInfoManager) {
        this.accountInfoManager = accountInfoManager;
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
    public void setCustomPasswordEncoder(
            CustomPasswordEncoder customPasswordEncoder) {
        this.customPasswordEncoder = customPasswordEncoder;
    }
}
