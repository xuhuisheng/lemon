package com.mossle.dict.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.dict.persistence.domain.DictType;

import org.springframework.stereotype.Service;

@Service
public class DictTypeManager extends HibernateEntityDao<DictType> {
}
