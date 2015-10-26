package com.mossle.pim.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.pim.persistence.domain.PimInfo;

import org.springframework.stereotype.Service;

@Service
public class PimInfoManager extends HibernateEntityDao<PimInfo> {
}
