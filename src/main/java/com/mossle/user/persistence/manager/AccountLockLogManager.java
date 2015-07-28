package com.mossle.user.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.user.persistence.domain.AccountLockLog;

import org.springframework.stereotype.Service;

@Service
public class AccountLockLogManager extends HibernateEntityDao<AccountLockLog> {
}
