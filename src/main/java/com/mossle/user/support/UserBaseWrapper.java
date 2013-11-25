package com.mossle.user.support;

import java.util.ArrayList;
import java.util.List;

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
}
