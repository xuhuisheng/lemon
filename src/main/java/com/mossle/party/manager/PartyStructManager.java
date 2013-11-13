package com.mossle.party.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.party.domain.PartyStruct;

import org.springframework.stereotype.Service;

@Service
public class PartyStructManager extends HibernateEntityDao<PartyStruct> {
}
