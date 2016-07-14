package com.mossle.sign.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.sign.persistence.domain.SignInfo;

import org.springframework.stereotype.Service;

@Service
public class SignInfoManager extends HibernateEntityDao<SignInfo> {
}
