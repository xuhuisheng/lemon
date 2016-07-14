package com.mossle.vote.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.vote.persistence.domain.VoteUser;

import org.springframework.stereotype.Service;

@Service
public class VoteUserManager extends HibernateEntityDao<VoteUser> {
}
