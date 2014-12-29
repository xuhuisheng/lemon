package com.mossle.humantask.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.humantask.persistence.domain.HtParticipant;

import org.springframework.stereotype.Service;

@Service
public class HtParticipantManager extends HibernateEntityDao<HtParticipant> {
}
