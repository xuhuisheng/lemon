package com.mossle.plm.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.plm.persistence.domain.PlmLog;

import org.springframework.stereotype.Service;

@Service
public class PlmLogManager extends HibernateEntityDao<PlmLog> {
}
