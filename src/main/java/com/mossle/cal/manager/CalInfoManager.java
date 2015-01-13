package com.mossle.cal.manager;

import com.mossle.cal.domain.CalInfo;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class CalInfoManager extends HibernateEntityDao<CalInfo> {
}
