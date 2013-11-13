package com.mossle.form.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.form.domain.DynamicModelData;

import org.springframework.stereotype.Service;

@Service
public class DynamicModelDataManager extends
        HibernateEntityDao<DynamicModelData> {
}
