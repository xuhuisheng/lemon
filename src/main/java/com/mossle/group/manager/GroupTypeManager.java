package com.mossle.group.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.group.domain.GroupType;

import org.springframework.stereotype.Service;

@Service
public class GroupTypeManager extends HibernateEntityDao<GroupType> {
}
