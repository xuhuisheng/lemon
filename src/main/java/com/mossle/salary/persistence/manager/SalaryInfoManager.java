package com.mossle.salary.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.salary.persistence.domain.SalaryInfo;

import org.springframework.stereotype.Service;

@Service
public class SalaryInfoManager extends HibernateEntityDao<SalaryInfo> {
}
