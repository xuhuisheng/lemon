package com.mossle.pim.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.pim.domain.PimInfo;

import org.springframework.stereotype.Service;

@Service
public class PimInfoManager extends HibernateEntityDao<PimInfo> {
}
