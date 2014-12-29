package com.mossle.dict.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.dict.persistence.domain.DictSchema;

import org.springframework.stereotype.Service;

@Service
public class DictSchemaManager extends HibernateEntityDao<DictSchema> {
}
