package com.mossle.report.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.report.persistence.domain.ReportDim;

import org.springframework.stereotype.Service;

@Service
public class ReportDimManager extends HibernateEntityDao<ReportDim> {
}
