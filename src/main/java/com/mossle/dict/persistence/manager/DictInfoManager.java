package com.mossle.dict.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.dict.persistence.domain.DictInfo;

import org.springframework.stereotype.Service;

@Service
public class DictInfoManager extends HibernateEntityDao<DictInfo> {
}
