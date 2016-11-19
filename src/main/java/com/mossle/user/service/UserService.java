package com.mossle.user.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.user.UserDTO;

import com.mossle.user.persistence.domain.UserAttr;
import com.mossle.user.persistence.domain.UserBase;
import com.mossle.user.persistence.domain.UserSchema;
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

    /**
     * 添加用户.
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
     */
    public void removeUser(UserBase userBase) {
        userBaseManager.removeAll(userBase.getUserAttrs());
        userBaseManager.remove(userBase);
        userPublisher.notifyUserRemoved(this.convertUserDto(userBase));
    }

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
}
