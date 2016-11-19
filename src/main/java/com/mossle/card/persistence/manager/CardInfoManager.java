package com.mossle.card.persistence.manager;

import com.mossle.card.persistence.domain.CardInfo;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class CardInfoManager extends HibernateEntityDao<CardInfo> {
}
