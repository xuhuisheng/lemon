package com.mossle.party.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.party.persistence.domain.PartyEntity;

import org.springframework.stereotype.Service;

@Service
public class PartyEntityManager extends HibernateEntityDao<PartyEntity> {
}
