package com.mossle.disk.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.disk.persistence.domain.DiskMember;

import org.springframework.stereotype.Service;

@Service
public class DiskMemberManager extends HibernateEntityDao<DiskMember> {
}
