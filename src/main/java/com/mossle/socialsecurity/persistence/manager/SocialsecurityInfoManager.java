package com.mossle.socialsecurity.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.socialsecurity.persistence.domain.SocialsecurityInfo;

import org.springframework.stereotype.Service;

@Service
public class SocialsecurityInfoManager extends
        HibernateEntityDao<SocialsecurityInfo> {
}
