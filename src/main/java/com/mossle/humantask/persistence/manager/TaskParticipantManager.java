package com.mossle.humantask.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.humantask.persistence.domain.TaskParticipant;

import org.springframework.stereotype.Service;

@Service
public class TaskParticipantManager extends HibernateEntityDao<TaskParticipant> {
}
