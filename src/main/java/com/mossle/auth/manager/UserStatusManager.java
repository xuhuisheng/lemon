package com.mossle.auth.manager;

import com.mossle.auth.domain.UserStatus;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class UserStatusManager extends HibernateEntityDao<UserStatus> {
}
