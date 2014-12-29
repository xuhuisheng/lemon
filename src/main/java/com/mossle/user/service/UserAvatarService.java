package com.mossle.user.service;

import java.io.ByteArrayOutputStream;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Map;

import javax.activation.DataSource;

import javax.annotation.Resource;

import com.mossle.api.internal.StoreConnector;
import com.mossle.api.internal.StoreDTO;
import com.mossle.api.scope.ScopeHolder;
import com.mossle.api.user.UserDTO;

import com.mossle.ext.store.ByteArrayDataSource;

import com.mossle.user.ImageUtils;
import com.mossle.user.component.UserPublisher;
import com.mossle.user.notification.DefaultUserNotification;
import com.mossle.user.notification.UserNotification;
import com.mossle.user.persistence.domain.UserAttr;
import com.mossle.user.persistence.domain.UserBase;
import com.mossle.user.persistence.domain.UserSchema;
import com.mossle.user.persistence.manager.UserAttrManager;
import com.mossle.user.persistence.manager.UserBaseManager;
import com.mossle.user.persistence.manager.UserRepoManager;
import com.mossle.user.persistence.manager.UserSchemaManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserAvatarService {
    private static Logger logger = LoggerFactory
            .getLogger(UserAvatarService.class);
    private UserBaseManager userBaseManager;
    private StoreConnector storeConnector;

    public DataSource viewAvatar(Long userId, int width) throws Exception {
        UserBase userBase = userBaseManager.get(userId);
        StoreDTO storeDto = null;

        if ((userBase == null) || (userBase.getAvatar() == null)) {
            storeDto = storeConnector.getStore("avatar", "default.jpg");

            return storeDto.getDataSource();
        }

        String key = userBase.getAvatar();
        storeDto = storeConnector.getStore("avatar", key);

        if (storeDto == null) {
            storeDto = storeConnector.getStore("avatar", "default.jpg");

            return storeDto.getDataSource();
        }

        if (width == 0) {
            return storeDto.getDataSource();
        }

        StoreDTO originalStoreDto = storeDto;
        String resizeKey = key + "-" + width;

        StoreDTO resizeStoreDto = storeConnector.getStore("avatar", resizeKey);

        if (resizeStoreDto == null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageUtils.zoomImage(originalStoreDto.getDataSource()
                    .getInputStream(), baos, width, width);
            logger.info("resizeKey : {}", resizeKey);
            resizeStoreDto = storeConnector.saveStore("avatar", resizeKey,
                    new ByteArrayDataSource(storeDto.getDataSource().getName(),
                            baos.toByteArray()));
        }

        return resizeStoreDto.getDataSource();
    }

    @Resource
    public void setUserBaseManager(UserBaseManager userBaseManager) {
        this.userBaseManager = userBaseManager;
    }

    @Resource
    public void setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;
    }
}
