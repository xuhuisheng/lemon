package com.mossle.stamp.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.stamp.persistence.domain.StampInfo;

import org.springframework.stereotype.Service;

@Service
public class StampInfoManager extends HibernateEntityDao<StampInfo> {
}
