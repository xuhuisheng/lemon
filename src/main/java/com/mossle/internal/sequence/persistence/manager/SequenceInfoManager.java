package com.mossle.internal.sequence.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.internal.sequence.persistence.domain.SequenceInfo;

import org.springframework.stereotype.Service;

@Service
public class SequenceInfoManager extends HibernateEntityDao<SequenceInfo> {
}
