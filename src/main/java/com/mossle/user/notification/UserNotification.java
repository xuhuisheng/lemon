package com.mossle.user.notification;

import com.mossle.user.persistence.domain.UserBase;

public interface UserNotification {
    void insertUser(UserBase userBase);

    void updateUser(UserBase userBase);

    void removeUser(UserBase userBase);
}
