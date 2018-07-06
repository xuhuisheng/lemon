package com.mossle.model.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.model.persistence.domain.ModelItem;

import org.springframework.stereotype.Service;

@Service
public class ModelItemManager extends HibernateEntityDao<ModelItem> {
}
