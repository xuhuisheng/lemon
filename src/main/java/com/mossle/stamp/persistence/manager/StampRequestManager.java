package com.mossle.stamp.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.stamp.persistence.domain.StampRequest;

import org.springframework.stereotype.Service;

@Service
public class StampRequestManager extends HibernateEntityDao<StampRequest> {
}
