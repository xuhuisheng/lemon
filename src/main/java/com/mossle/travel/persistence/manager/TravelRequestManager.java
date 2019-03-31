package com.mossle.travel.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.travel.persistence.domain.TravelRequest;

import org.springframework.stereotype.Service;

@Service
public class TravelRequestManager extends HibernateEntityDao<TravelRequest> {
}
