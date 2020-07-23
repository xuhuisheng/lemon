package com.mossle.travel.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.travel.persistence.domain.TravelItem;

import org.springframework.stereotype.Service;

@Service
public class TravelItemManager extends HibernateEntityDao<TravelItem> {
}
