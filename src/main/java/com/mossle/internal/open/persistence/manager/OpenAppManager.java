package com.mossle.internal.open.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.internal.open.persistence.domain.OpenApp;

import org.springframework.stereotype.Service;

@Service
public class OpenAppManager extends HibernateEntityDao<OpenApp> {
}
