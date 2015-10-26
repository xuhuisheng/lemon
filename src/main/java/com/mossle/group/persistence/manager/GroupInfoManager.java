package com.mossle.group.persistence.manager;

import java.util.List;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.group.persistence.domain.GroupInfo;

import org.springframework.stereotype.Service;

@Service
public class GroupInfoManager extends HibernateEntityDao<GroupInfo> {
}
