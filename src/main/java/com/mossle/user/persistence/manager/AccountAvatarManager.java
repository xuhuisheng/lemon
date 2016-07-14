package com.mossle.user.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.user.persistence.domain.AccountAvatar;

import org.springframework.stereotype.Service;

@Service
public class AccountAvatarManager extends HibernateEntityDao<AccountAvatar> {
}
