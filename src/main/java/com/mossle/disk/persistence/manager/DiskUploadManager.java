package com.mossle.disk.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.disk.persistence.domain.DiskUpload;

import org.springframework.stereotype.Service;

@Service
public class DiskUploadManager extends HibernateEntityDao<DiskUpload> {
}
