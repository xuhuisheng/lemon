package com.mossle.form.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.form.domain.FormTemplate;

import org.springframework.stereotype.Service;

@Service
public class FormTemplateManager extends HibernateEntityDao<FormTemplate> {
}
