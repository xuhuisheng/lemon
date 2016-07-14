package com.mossle.vote.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.vote.persistence.domain.VoteItem;

import org.springframework.stereotype.Service;

@Service
public class VoteItemManager extends HibernateEntityDao<VoteItem> {
}
