package com.mossle.plm.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.plm.persistence.domain.PlmComponent;

import org.springframework.stereotype.Service;

@Service
public class PlmComponentManager extends HibernateEntityDao<PlmComponent> {
}
