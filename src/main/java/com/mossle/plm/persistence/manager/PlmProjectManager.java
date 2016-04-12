package com.mossle.plm.persistence.manager;

import java.util.List;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.plm.persistence.domain.PlmProject;

import org.springframework.stereotype.Service;

@Service
public class PlmProjectManager extends HibernateEntityDao<PlmProject> {
}
