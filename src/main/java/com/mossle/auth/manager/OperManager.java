package com.mossle.auth.manager;

import com.mossle.auth.domain.Oper;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class OperManager extends HibernateEntityDao<Oper> {
}
