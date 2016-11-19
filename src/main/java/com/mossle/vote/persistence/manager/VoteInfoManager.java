package com.mossle.vote.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.vote.persistence.domain.VoteInfo;

import org.springframework.stereotype.Service;

@Service
public class VoteInfoManager extends HibernateEntityDao<VoteInfo> {
}
