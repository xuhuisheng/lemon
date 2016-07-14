package com.mossle.group.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.group.persistence.domain.GroupType;

import org.springframework.stereotype.Service;

@Service
public class GroupTypeManager extends HibernateEntityDao<GroupType> {
}
