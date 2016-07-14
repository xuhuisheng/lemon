package com.mossle.plm.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.plm.persistence.domain.PlmIssue;

import org.springframework.stereotype.Service;

@Service
public class PlmIssueManager extends HibernateEntityDao<PlmIssue> {
}
