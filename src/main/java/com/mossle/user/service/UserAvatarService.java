package com.mossle.user.service;

import java.io.ByteArrayOutputStream;

import javax.activation.DataSource;

import javax.annotation.Resource;

import com.mossle.api.store.StoreConnector;
import com.mossle.api.store.StoreDTO;

import com.mossle.core.store.ByteArrayDataSource;

import com.mossle.user.ImageUtils;
import com.mossle.user.avatar.AvatarCache;
import com.mossle.user.persistence.domain.AccountAvatar;
import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.manager.AccountAvatarManager;
import com.mossle.user.persistence.manager.AccountInfoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserAvatarService {
    private static Logger logger = LoggerFactory
            .getLogger(UserAvatarService.class);
    private AccountInfoManager accountInfoManager;
    private AccountAvatarManager accountAvatarManager;
    private StoreConnector storeConnector;
    private AvatarCache avatarCache;

    public DataSource viewAvatarById(Long accountId, int width, String tenantId)
            throws Exception {
        if (accountId == null) {
            logger.info("accountId cannot be null");

            return null;
        }

        String key = "accountId:" + accountId + ":" + width;
        String userId = Long.toString(accountId);
        DataSource dataSource = this.avatarCache.getDataSource(userId, width);

        if (dataSource != null) {
            return dataSource;
        }

        AccountInfo accountInfo = accountInfoManager.get(accountId);

        dataSource = this.viewAvatarByAccountInfo(accountInfo, width, tenantId);
        this.avatarCache.updateDataSource(userId, width, dataSource);

        return dataSource;
    }

    public DataSource viewAvatarByUsername(String username, int width,
            String tenantId) throws Exception {
        // String key = "username:" + username + ":" + width;
        AccountInfo accountInfo = accountInfoManager.findUniqueBy("username",
                username);
        String userId = Long.toString(accountInfo.getId());
        DataSource dataSource = this.avatarCache.getDataSource(userId, width);

        if (dataSource != null) {
            return dataSource;
        }

        dataSource = this.viewAvatarByAccountInfo(accountInfo, width, tenantId);
        this.avatarCache.updateDataSource(userId, width, dataSource);

        return dataSource;
    }

    public DataSource viewAvatarByAccountInfo(AccountInfo accountInfo,
            int width, String tenantId) throws Exception {
        String key = null;

        if (accountInfo != null) {
            String hql = "from AccountAvatar where accountInfo=? and type='default'";
            AccountAvatar accountAvatar = accountAvatarManager.findUnique(hql,
                    accountInfo);

            if (accountAvatar != null) {
                key = accountAvatar.getCode();
            }
        }

        if (key == null) {
            key = "default.jpg";
        }

        StoreDTO storeDto = null;

        storeDto = storeConnector.getStore("avatar", key, tenantId);

        if (storeDto == null) {
            storeDto = storeConnector.getStore("avatar", "default.jpg",
                    tenantId);

            return storeDto.getDataSource();
        }

        if (width == 0) {
            return storeDto.getDataSource();
        }

        StoreDTO originalStoreDto = storeDto;
        int index = key.lastIndexOf(".");
        String prefix = key.substring(0, index);
        String suffix = key.substring(index);
        String resizeKey = prefix + "-" + width + suffix;

        StoreDTO resizeStoreDto = storeConnector.getStore("avatar", resizeKey,
                tenantId);

        if (resizeStoreDto == null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageUtils.zoomImage(originalStoreDto.getDataSource()
                    .getInputStream(), baos, width, width);
            logger.info("resizeKey : {}", resizeKey);
            resizeStoreDto = storeConnector.saveStore("avatar", resizeKey,
                    new ByteArrayDataSource(storeDto.getDataSource().getName(),
                            baos.toByteArray()), tenantId);
        }

        return resizeStoreDto.getDataSource();
    }

    @Resource
    public void setAccountInfoManager(AccountInfoManager accountInfoManager) {
        this.accountInfoManager = accountInfoManager;
    }

    @Resource
    public void setAccountAvatarManager(
            AccountAvatarManager accountAvatarManager) {
        this.accountAvatarManager = accountAvatarManager;
    }

    @Resource
    public void setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;
    }

    @Resource
    public void setAvatarCache(AvatarCache avatarCache) {
        this.avatarCache = avatarCache;
    }
}
