package com.mossle.model.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.model.persistence.domain.ModelBase;

import org.springframework.stereotype.Service;

@Service
public class ModelBaseManager extends HibernateEntityDao<ModelBase> {
}
