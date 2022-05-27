package com.mossle.user.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.user.persistence.domain.AccountLog;

import org.springframework.stereotype.Repository;

@Repository
public class AccountLogManager extends HibernateEntityDao<AccountLog> {
}
