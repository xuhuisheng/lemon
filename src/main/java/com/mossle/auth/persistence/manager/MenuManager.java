package com.mossle.auth.persistence.manager;

import com.mossle.auth.persistence.domain.Menu;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class MenuManager extends HibernateEntityDao<Menu> {
}
