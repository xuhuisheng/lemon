package com.mossle.user.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.user.persistence.domain.AccountLockLog;

import org.springframework.stereotype.Repository;

@Repository
public class AccountLockLogManager extends HibernateEntityDao<AccountLockLog> {
}
