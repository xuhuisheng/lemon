package com.mossle.officesupply.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.officesupply.persistence.domain.OfficesupplyInfo;

import org.springframework.stereotype.Service;

@Service
public class OfficesupplyInfoManager extends
        HibernateEntityDao<OfficesupplyInfo> {
}
