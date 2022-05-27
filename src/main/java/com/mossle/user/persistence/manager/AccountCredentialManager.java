package com.mossle.user.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.user.persistence.domain.AccountCredential;

import org.springframework.stereotype.Repository;

@Repository
public class AccountCredentialManager extends
        HibernateEntityDao<AccountCredential> {
}
