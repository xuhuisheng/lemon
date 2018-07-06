package com.mossle.travel.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.travel.persistence.domain.TravelInfo;

import org.springframework.stereotype.Service;

@Service
public class TravelInfoManager extends HibernateEntityDao<TravelInfo> {
}
