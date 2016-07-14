package com.mossle.plm.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.plm.persistence.domain.PlmStep;

import org.springframework.stereotype.Service;

@Service
public class PlmStepManager extends HibernateEntityDao<PlmStep> {
}
