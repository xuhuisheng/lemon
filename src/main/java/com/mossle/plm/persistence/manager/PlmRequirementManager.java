package com.mossle.plm.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.plm.persistence.domain.PlmRequirement;

import org.springframework.stereotype.Service;

@Service
public class PlmRequirementManager extends HibernateEntityDao<PlmRequirement> {
}
