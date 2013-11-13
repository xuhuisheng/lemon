package com.mossle.bpm.persistence.manager;

import com.mossle.bpm.persistence.domain.BpmCategory;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class BpmCategoryManager extends HibernateEntityDao<BpmCategory> {
}
