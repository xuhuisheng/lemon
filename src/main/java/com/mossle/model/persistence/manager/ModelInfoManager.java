package com.mossle.model.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.model.persistence.domain.ModelInfo;

import org.springframework.stereotype.Service;

@Service
public class ModelInfoManager extends HibernateEntityDao<ModelInfo> {
}
