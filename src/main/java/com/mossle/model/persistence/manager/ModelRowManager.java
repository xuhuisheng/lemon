package com.mossle.model.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.model.persistence.domain.ModelRow;

import org.springframework.stereotype.Service;

@Service
public class ModelRowManager extends HibernateEntityDao<ModelRow> {
}
