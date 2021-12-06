package com.mossle.disk.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.disk.persistence.domain.DiskTagInfo;

import org.springframework.stereotype.Service;

@Service
public class DiskTagInfoManager extends HibernateEntityDao<DiskTagInfo> {
}
