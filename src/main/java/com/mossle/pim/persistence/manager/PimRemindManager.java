package com.mossle.pim.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.pim.persistence.domain.PimRemind;

import org.springframework.stereotype.Service;

@Service
public class PimRemindManager extends HibernateEntityDao<PimRemind> {
}
