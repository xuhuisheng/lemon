package com.mossle.user.avatar;

import java.io.InputStream;

import javax.activation.DataSource;

import javax.annotation.Resource;

import com.mossle.api.avatar.AvatarConnector;
import com.mossle.api.avatar.AvatarDTO;

import com.mossle.core.mapper.BeanMapper;

import com.mossle.user.persistence.domain.AccountAvatar;
import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.manager.AccountAvatarManager;
import com.mossle.user.persistence.manager.AccountInfoManager;
import com.mossle.user.service.UserAvatarService;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AvatarConnectorImpl implements AvatarConnector {
    private static Logger logger = LoggerFactory
            .getLogger(AvatarConnectorImpl.class);
    private UserAvatarService userAvatarService;
    private AccountAvatarManager accountAvatarManager;
    private AccountInfoManager accountInfoManager;
    private AvatarCache avatarCache;
    private BeanMapper beanMapper = new BeanMapper();

    public AvatarDTO findAvatar(String userId) {
        try {
            Long accountId = Long.parseLong(userId);
            String hql = "from AccountAvatar where accountInfo.id=? and type='default'";
            AccountAvatar accountAvatar = accountAvatarManager.findUnique(hql,
                    accountId);

            if (accountAvatar == null) {
                logger.info("cannot find avatar : {}", accountId);

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
            Long accountId = Long.parseLong(userId);
            String hql = "from AccountAvatar where accountInfo.id=? and type='default'";
            AccountAvatar accountAvatar = accountAvatarManager.findUnique(hql,
                    accountId);

            if (accountAvatar == null) {
                AccountInfo accountInfo = accountInfoManager.get(accountId);

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
    public void setAccountInfoManager(AccountInfoManager accountInfoManager) {
        this.accountInfoManager = accountInfoManager;
    }

    @Resource
    public void setAvatarCache(AvatarCache avatarCache) {
        this.avatarCache = avatarCache;
    }
}
