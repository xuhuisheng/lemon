package com.mossle.group.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.group.domain.GroupInfo;

import org.springframework.stereotype.Service;

@Service
public class GroupInfoManager extends HibernateEntityDao<GroupInfo> {
}
