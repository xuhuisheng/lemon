package com.mossle.internal.template.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.internal.template.persistence.domain.TemplateField;

import org.springframework.stereotype.Service;

@Service
public class TemplateFieldManager extends HibernateEntityDao<TemplateField> {
}
