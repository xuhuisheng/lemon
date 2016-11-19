package com.mossle.officesupply.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.officesupply.persistence.domain.OfficesupplyReceive;

import org.springframework.stereotype.Service;

@Service
public class OfficesupplyReceiveManager extends
        HibernateEntityDao<OfficesupplyReceive> {
}
