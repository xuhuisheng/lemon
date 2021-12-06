package com.mossle.disk.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.disk.persistence.domain.DiskDownload;

import org.springframework.stereotype.Service;

@Service
public class DiskDownloadManager extends HibernateEntityDao<DiskDownload> {
}
