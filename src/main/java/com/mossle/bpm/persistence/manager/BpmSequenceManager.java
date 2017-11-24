package com.mossle.bpm.persistence.manager;

import com.mossle.bpm.persistence.domain.BpmSequence;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class BpmSequenceManager extends HibernateEntityDao<BpmSequence> {
}
