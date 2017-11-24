package com.mossle.report.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.report.persistence.domain.ReportQuery;

import org.springframework.stereotype.Service;

@Service
public class ReportQueryManager extends HibernateEntityDao<ReportQuery> {
}
