package com.mossle.train.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.train.persistence.domain.TrainInfo;

import org.springframework.stereotype.Service;

@Service
public class TrainInfoManager extends HibernateEntityDao<TrainInfo> {
}
