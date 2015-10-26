package com.mossle.form.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.form.persistence.domain.FormTemplate;

import org.springframework.stereotype.Service;

@Service
public class FormTemplateManager extends HibernateEntityDao<FormTemplate> {
}
