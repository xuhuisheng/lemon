package com.mossle.model.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.model.persistence.domain.ModelCategory;

import org.springframework.stereotype.Service;

@Service
public class ModelCategoryManager extends HibernateEntityDao<ModelCategory> {
}
