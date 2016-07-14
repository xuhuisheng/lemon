package com.mossle.humantask.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.humantask.persistence.domain.HtHumantask;

import org.springframework.stereotype.Service;

@Service
public class HtHumantaskManager extends HibernateEntityDao<HtHumantask> {
}
