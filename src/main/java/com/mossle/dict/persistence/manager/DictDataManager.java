package com.mossle.dict.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.dict.persistence.domain.DictData;

import org.springframework.stereotype.Service;

@Service
public class DictDataManager extends HibernateEntityDao<DictData> {
}
