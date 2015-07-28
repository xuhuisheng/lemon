package com.mossle.user.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.user.persistence.domain.AccountLockInfo;

import org.springframework.stereotype.Service;

@Service
public class AccountLockInfoManager extends HibernateEntityDao<AccountLockInfo> {
}
