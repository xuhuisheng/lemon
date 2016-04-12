package com.mossle.user.support;

import java.io.InputStream;

import javax.activation.DataSource;

import javax.annotation.Resource;

import com.mossle.api.user.UserAvatarConnector;

import com.mossle.user.service.UserAvatarService;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserAvatarConnectorImpl implements UserAvatarConnector {
    private static Logger logger = LoggerFactory
            .getLogger(UserAvatarConnectorImpl.class);
    private UserAvatarService userAvatarService;

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
}
