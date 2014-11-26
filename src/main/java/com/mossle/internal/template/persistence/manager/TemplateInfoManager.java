package com.mossle.internal.template.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.internal.template.persistence.domain.TemplateInfo;

import org.springframework.stereotype.Service;

@Service
public class TemplateInfoManager extends HibernateEntityDao<TemplateInfo> {
}
