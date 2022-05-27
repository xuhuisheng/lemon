package com.mossle.user.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.user.persistence.domain.AccountOnline;

import org.springframework.stereotype.Repository;

@Repository
public class AccountOnlineManager extends HibernateEntityDao<AccountOnline> {
}
