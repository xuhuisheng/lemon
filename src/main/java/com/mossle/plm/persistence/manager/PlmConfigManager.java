package com.mossle.plm.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.plm.persistence.domain.PlmConfig;

import org.springframework.stereotype.Service;

@Service
public class PlmConfigManager extends HibernateEntityDao<PlmConfig> {
}
