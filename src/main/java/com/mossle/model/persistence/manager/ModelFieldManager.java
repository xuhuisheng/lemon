package com.mossle.model.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.model.persistence.domain.ModelField;

import org.springframework.stereotype.Service;

@Service
public class ModelFieldManager extends HibernateEntityDao<ModelField> {
}
