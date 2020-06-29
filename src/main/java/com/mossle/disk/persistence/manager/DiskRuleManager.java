package com.mossle.disk.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.disk.persistence.domain.DiskRule;

import org.springframework.stereotype.Service;

@Service
public class DiskRuleManager extends HibernateEntityDao<DiskRule> {
}
