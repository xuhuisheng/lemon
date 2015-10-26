package com.mossle.party.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.party.persistence.domain.PartyType;

import org.springframework.stereotype.Service;

@Service
public class PartyTypeManager extends HibernateEntityDao<PartyType> {
}
