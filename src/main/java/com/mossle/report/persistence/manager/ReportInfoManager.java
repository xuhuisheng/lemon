package com.mossle.report.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.report.persistence.domain.ReportInfo;

import org.springframework.stereotype.Service;

@Service
public class ReportInfoManager extends HibernateEntityDao<ReportInfo> {
}
