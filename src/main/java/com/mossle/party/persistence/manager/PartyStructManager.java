package com.mossle.party.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.party.persistence.domain.PartyStruct;

import org.springframework.stereotype.Service;

@Service
public class PartyStructManager extends HibernateEntityDao<PartyStruct> {
}
