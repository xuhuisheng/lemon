package com.mossle.disk.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.disk.persistence.domain.DiskFavorite;

import org.springframework.stereotype.Service;

@Service
public class DiskFavoriteManager extends HibernateEntityDao<DiskFavorite> {
}
