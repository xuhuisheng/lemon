package com.mossle.plm.persistence.manager;

import java.util.List;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.plm.persistence.domain.PlmVersion;

import org.springframework.stereotype.Service;

@Service
public class PlmVersionManager extends HibernateEntityDao<PlmVersion> {
}
