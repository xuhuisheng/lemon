package com.mossle.pim.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.pim.persistence.domain.PimNote;

import org.springframework.stereotype.Service;

@Service
public class PimNoteManager extends HibernateEntityDao<PimNote> {
}
