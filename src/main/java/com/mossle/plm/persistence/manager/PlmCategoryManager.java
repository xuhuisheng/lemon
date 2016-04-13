package com.mossle.plm.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.plm.persistence.domain.PlmCategory;

import org.springframework.stereotype.Service;

@Service
public class PlmCategoryManager extends HibernateEntityDao<PlmCategory> {
}
