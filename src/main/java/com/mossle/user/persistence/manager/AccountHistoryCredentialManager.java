package com.mossle.user.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.user.persistence.domain.AccountHistoryCredential;

import org.springframework.stereotype.Service;

@Service
public class AccountHistoryCredentialManager extends
        HibernateEntityDao<AccountHistoryCredential> {
}
