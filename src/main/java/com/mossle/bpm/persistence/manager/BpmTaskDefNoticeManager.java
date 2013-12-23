package com.mossle.bpm.persistence.manager;

import com.mossle.bpm.persistence.domain.BpmTaskDefNotice;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class BpmTaskDefNoticeManager extends
        HibernateEntityDao<BpmTaskDefNotice> {
}
