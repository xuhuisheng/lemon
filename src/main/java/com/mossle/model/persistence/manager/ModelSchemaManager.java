package com.mossle.model.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.model.persistence.domain.ModelSchema;

import org.springframework.stereotype.Service;

@Service
public class ModelSchemaManager extends HibernateEntityDao<ModelSchema> {
}
