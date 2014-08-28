package com.mossle.user.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mossle.core.mapper.BeanMapper;

import com.mossle.user.persistence.domain.UserAttr;
import com.mossle.user.persistence.domain.UserBase;
import com.mossle.user.persistence.domain.UserSchema;

public class UserBaseWrapper extends UserBase {
    private transient BeanMapper beanMapper = new BeanMapper();
    private transient List<UserAttrWrapper> userAttrWrappers = new ArrayList<UserAttrWrapper>();

    public UserBaseWrapper() {
    }

    public UserBaseWrapper(UserBase userBase) {
        beanMapper.copy(userBase, this);

        if (userBase.getUserRepo() == null) {
            return;
        }

        for (UserSchema userSchema : userBase.getUserRepo().getUserSchemas()) {
            boolean notFound = true;

            for (UserAttr userAttr : userBase.getUserAttrs()) {
                if (userAttr.getUserSchema().getId().equals(userSchema.getId())) {
                    notFound = false;
                    userAttrWrappers.add(new UserAttrWrapper(userAttr));

                    break;
                }
            }

            if (notFound) {
                userAttrWrappers.add(new UserAttrWrapper(userSchema));
            }
        }
    }

    public List<UserAttrWrapper> getUserAttrWrappers() {
        return userAttrWrappers;
    }

    // ~ ======================================================================
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        this.copyUserBase(map, this);

        for (UserAttrWrapper userAttrWrapper : userAttrWrappers) {
            this.copyUserAttr(map, userAttrWrapper);
        }

        return map;
    }

    public void copyUserBase(Map<String, Object> map, UserBase userBase) {
        map.put("id", userBase.getId());
        map.put("username", userBase.getUsername());
        map.put("displayName", userBase.getDisplayName());
        map.put("email", userBase.getEmail());
        map.put("mobile", userBase.getMobile());
        map.put("ref", userBase.getRef());
        map.put("status", userBase.getStatus());
    }

    public void copyUserAttr(Map<String, Object> map,
            UserAttrWrapper userAttrWrapper) {
        map.put(userAttrWrapper.getCode(), userAttrWrapper.getValue());
    }
}
