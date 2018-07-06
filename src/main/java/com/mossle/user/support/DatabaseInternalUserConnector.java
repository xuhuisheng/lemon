package com.mossle.user.support;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataSource;

import javax.annotation.Resource;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.avatar.AvatarDTO;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.AccountStatus;

import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.query.PropertyFilterUtils;

import com.mossle.spi.device.DeviceDTO;
import com.mossle.spi.user.InternalUserConnector;

import com.mossle.user.avatar.AvatarCache;
import com.mossle.user.persistence.domain.AccountAlias;
import com.mossle.user.persistence.domain.AccountAvatar;
import com.mossle.user.persistence.domain.AccountCredential;
import com.mossle.user.persistence.domain.AccountDevice;
import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.domain.AccountLockInfo;
import com.mossle.user.persistence.manager.AccountAliasManager;
import com.mossle.user.persistence.manager.AccountAvatarManager;
import com.mossle.user.persistence.manager.AccountCredentialManager;
import com.mossle.user.persistence.manager.AccountDeviceManager;
import com.mossle.user.persistence.manager.AccountInfoManager;
import com.mossle.user.persistence.manager.AccountLockInfoManager;
import com.mossle.user.service.UserAvatarService;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.util.Assert;

public class DatabaseInternalUserConnector implements InternalUserConnector {
    private static Logger logger = LoggerFactory
            .getLogger(DatabaseInternalUserConnector.class);
    private JdbcTemplate jdbcTemplate;
    private String sqlAlias = "SELECT ai.USERNAME FROM ACCOUNT_ALIAS aa, ACCOUNT_INFO ai WHERE aa.ACCOUNT_ID=ai.ID AND aa.NAME=?";
    private String sqlFindPassword = "SELECT AC.PASSWORD AS PASSWORD"
            + " FROM ACCOUNT_CREDENTIAL AC,ACCOUNT_INFO AI"
            + " WHERE AC.ACCOUNT_ID=AI.ID AND CATALOG='default' AND AI.USERNAME=? and AI.TENANT_ID=?";
    private AccountAliasManager accountAliasManager;
    private AccountInfoManager accountInfoManager;
    private AccountDeviceManager accountDeviceManager;
    private CurrentUserHolder currentUserHolder;
    private TenantHolder tenantHolder;
    private UserAvatarService userAvatarService;
    private AccountAvatarManager accountAvatarManager;
    private AvatarCache avatarCache;
    private BeanMapper beanMapper = new BeanMapper();
    private AccountLockInfoManager accountLockInfoManager;
    private AccountCredentialManager accountCredentialManager;

    public String findUsernameByAlias(String alias) {
        Assert.hasText(alias, "alias should not be null");

        try {
            logger.debug("alias : {}", alias);

            String username = jdbcTemplate.queryForObject(sqlAlias,
                    String.class, alias);
            logger.debug("username : {}", username);

            return username;
        } catch (EmptyResultDataAccessException ex) {
            logger.debug(ex.getMessage(), ex);
            logger.debug("user[{}] is not exists.", alias);

            return alias.trim().toLowerCase();
        }
    }

    public void updateAlias(String username, String type, String alias) {
        AccountInfo accountInfo = accountInfoManager.findUniqueBy("username",
                username);

        if (accountInfo == null) {
            return;
        }

        AccountAlias accountAlias = accountAliasManager.findUnique(
                "from AccountAlias where type=? and accountInfo=?", type,
                accountInfo);

        if (accountAlias == null) {
            accountAlias = new AccountAlias();
            accountAlias.setAccountInfo(accountInfo);
            accountAlias.setType(type);
            accountAlias.setName(alias);
        }

        accountAlias.setName(alias);
        accountAliasManager.save(accountAlias);
    }

    public String findPassword(String username, String tenantId) {
        if (username == null) {
            logger.info("username is null");

            return null;
        }

        username = username.toLowerCase();

        String password = null;

        try {
            password = jdbcTemplate.queryForObject(sqlFindPassword,
                    String.class, username, tenantId);
        } catch (Exception ex) {
            logger.info(ex.getMessage());
            logger.info("cannot find password : {}, {}", username, tenantId);
        }

        return password;
    }

    public DeviceDTO findDevice(String code) {
        if (code == null) {
            return null;
        }

        AccountDevice accountDevice = accountDeviceManager.findUniqueBy("code",
                code);

        if (accountDevice == null) {
            return null;
        }

        DeviceDTO deviceDto = new DeviceDTO();
        deviceDto.setCode(accountDevice.getCode());
        deviceDto.setType(accountDevice.getType());
        deviceDto.setOs(accountDevice.getOs());
        deviceDto.setClient(accountDevice.getClient());
        deviceDto.setStatus(accountDevice.getStatus());

        return deviceDto;
    }

    public void saveDevice(DeviceDTO deviceDto) {
        if (deviceDto == null) {
            return;
        }

        if (deviceDto.getCode() == null) {
            return;
        }

        AccountDevice accountDevice = accountDeviceManager.findUniqueBy("code",
                deviceDto.getCode());
        Date now = new Date();

        if (accountDevice == null) {
            accountDevice = new AccountDevice();
            accountDevice.setCode(deviceDto.getCode());
            accountDevice.setType(deviceDto.getType());
            accountDevice.setOs(deviceDto.getOs());
            accountDevice.setClient(deviceDto.getClient());
            accountDevice.setCreateTime(now);
            accountDevice.setLastLoginTime(now);
            accountDevice.setStatus("new");
            accountDevice.setTenantId(tenantHolder.getTenantId());

            AccountInfo accountInfo = accountInfoManager.get(Long
                    .parseLong(currentUserHolder.getUserId()));
            accountDevice.setAccountInfo(accountInfo);
        } else {
            accountDevice.setLastLoginTime(now);
        }

        accountDeviceManager.save(accountDevice);
    }

    public AvatarDTO findAvatar(String userId) {
        try {
            AccountInfo accountInfo = accountInfoManager.findUniqueBy("code",
                    userId);
            String hql = "from AccountAvatar where accountInfo=? and type='default'";
            AccountAvatar accountAvatar = accountAvatarManager.findUnique(hql,
                    accountInfo);

            if (accountAvatar == null) {
                logger.info("cannot find avatar : {}", userId);

                return null;
            }

            AvatarDTO avatarDto = new AvatarDTO();
            beanMapper.copy(accountAvatar, avatarDto);

            return avatarDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return null;
    }

    public void saveAvatar(String userId, String code) {
        try {
            AccountInfo accountInfo = accountInfoManager.findUniqueBy("code",
                    userId);
            String hql = "from AccountAvatar where accountInfo=? and type='default'";
            AccountAvatar accountAvatar = accountAvatarManager.findUnique(hql,
                    accountInfo);

            if (accountAvatar == null) {
                accountAvatar = new AccountAvatar();
                accountAvatar.setAccountInfo(accountInfo);
                accountAvatar.setType("default");
            }

            accountAvatar.setCode(code);
            accountAvatarManager.save(accountAvatar);
            this.avatarCache.removeDataSource(userId);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public InputStream findAvatarInputStream(String userId) {
        try {
            Long accountId = Long.parseLong(userId);
            DataSource dataSource = userAvatarService.viewAvatarById(accountId,
                    35, "1");

            return dataSource.getInputStream();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return null;
    }

    public String findAvatarBase64(String userId) {
        InputStream is = this.findAvatarInputStream(userId);

        if (is == null) {
            return null;
        }

        try {
            byte[] bytes = IOUtils.toByteArray(is);
            String text = Base64.encodeBase64String(bytes);

            return text;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return null;
    }

    public boolean isLocked(String username, String application) {
        AccountLockInfo accountLockInfo = accountLockInfoManager.findUnique(
                "from AccountLockInfo where username=? and type=?", username,
                application);

        return accountLockInfo != null;
    }

    public String getAccountStatus(String username, String application) {
        AccountInfo accountInfo = accountInfoManager.findUnique(
                "from AccountInfo where username=?", username);

        if (!"1".equals(accountInfo.getStatus())) {
            return AccountStatus.ACCOUNT_DISABLED;
        }

        Date now = new Date();

        try {
            // account expire
            Date accountExpireDate = accountInfo.getCloseTime();

            if ((accountExpireDate != null) && accountExpireDate.before(now)) {
                return AccountStatus.ACCOUNT_EXPIRED;
            }
        } catch (Exception ex) {
            logger.debug(ex.getMessage(), ex);
        }

        // TODO: password must change
        try {
            AccountCredential accountCredential = accountCredentialManager
                    .findUnique("from AccountCredential from accountInfo=? and catalog='normal'");

            // password expire
            Date passwordExpireDate = accountCredential.getExpireTime();

            if ((passwordExpireDate != null) && passwordExpireDate.before(now)) {
                return AccountStatus.PASSWORD_EXPIRED;
            }
        } catch (Exception ex) {
            logger.debug(ex.getMessage(), ex);
        }

        return AccountStatus.ENABLED;
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Resource
    public void setAccountInfoManager(AccountInfoManager accountInfoManager) {
        this.accountInfoManager = accountInfoManager;
    }

    @Resource
    public void setAccountAliasManager(AccountAliasManager accountAliasManager) {
        this.accountAliasManager = accountAliasManager;
    }

    @Resource
    public void setAccountDeviceManager(
            AccountDeviceManager accountDeviceManager) {
        this.accountDeviceManager = accountDeviceManager;
    }

    public void setSqlAlias(String sqlAlias) {
        this.sqlAlias = sqlAlias;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setUserAvatarService(UserAvatarService userAvatarService) {
        this.userAvatarService = userAvatarService;
    }

    @Resource
    public void setAccountAvatarManager(
            AccountAvatarManager accountAvatarManager) {
        this.accountAvatarManager = accountAvatarManager;
    }

    @Resource
    public void setAvatarCache(AvatarCache avatarCache) {
        this.avatarCache = avatarCache;
    }

    @Resource
    public void setAccountLockInfoManager(
            AccountLockInfoManager accountLockInfoManager) {
        this.accountLockInfoManager = accountLockInfoManager;
    }

    @Resource
    public void setAccountCredentialManager(
            AccountCredentialManager accountCredentialManager) {
        this.accountCredentialManager = accountCredentialManager;
    }
}
