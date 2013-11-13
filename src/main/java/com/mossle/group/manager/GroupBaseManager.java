package com.mossle.group.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.group.domain.GroupBase;

import org.springframework.stereotype.Service;

@Service
public class GroupBaseManager extends HibernateEntityDao<GroupBase> {
}
