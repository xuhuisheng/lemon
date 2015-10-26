package com.mossle.pim.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.pim.persistence.domain.PimFavorite;

import org.springframework.stereotype.Service;

@Service
public class PimFavoriteManager extends HibernateEntityDao<PimFavorite> {
}
