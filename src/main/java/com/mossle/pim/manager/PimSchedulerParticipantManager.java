package com.mossle.pim.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.pim.domain.PimSchedulerParticipant;

import org.springframework.stereotype.Service;

@Service
public class PimSchedulerParticipantManager extends
        HibernateEntityDao<PimSchedulerParticipant> {
}
