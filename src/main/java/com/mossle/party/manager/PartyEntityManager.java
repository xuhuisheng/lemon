package com.mossle.party.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.party.domain.PartyEntity;

import org.springframework.stereotype.Service;

@Service
public class PartyEntityManager extends HibernateEntityDao<PartyEntity> {
}
