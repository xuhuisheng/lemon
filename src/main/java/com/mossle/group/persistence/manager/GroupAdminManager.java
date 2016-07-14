package com.mossle.group.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.group.persistence.domain.GroupAdmin;

import org.springframework.stereotype.Service;

@Service
public class GroupAdminManager extends HibernateEntityDao<GroupAdmin> {
}
