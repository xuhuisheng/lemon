package com.mossle.user.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.user.persistence.domain.AccountHistoryCredential;

import org.springframework.stereotype.Repository;

@Repository
public class AccountHistoryCredentialManager extends
        HibernateEntityDao<AccountHistoryCredential> {
}
