package com.mossle.party.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.party.domain.PartyType;

import org.springframework.stereotype.Service;

@Service
public class PartyTypeManager extends HibernateEntityDao<PartyType> {
}
