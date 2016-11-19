package com.mossle.pim.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.pim.persistence.domain.PimPhrase;

import org.springframework.stereotype.Service;

@Service
public class PimPhraseManager extends HibernateEntityDao<PimPhrase> {
}
