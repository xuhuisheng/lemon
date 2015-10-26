package com.mossle.group.persistence.manager;

import java.util.List;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.group.persistence.domain.GroupMember;

import org.springframework.stereotype.Service;

@Service
public class GroupMemberManager extends HibernateEntityDao<GroupMember> {
}
