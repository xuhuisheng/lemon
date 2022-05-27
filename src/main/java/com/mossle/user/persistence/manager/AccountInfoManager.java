package com.mossle.user.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.user.persistence.domain.AccountInfo;

import org.springframework.stereotype.Repository;

@Repository
public class AccountInfoManager extends HibernateEntityDao<AccountInfo> {
}
