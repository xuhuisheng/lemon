package com.mossle.pim.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.pim.persistence.domain.WorkReportAttachment;

import org.springframework.stereotype.Service;

@Service
public class WorkReportAttachmentManager extends
        HibernateEntityDao<WorkReportAttachment> {
}
