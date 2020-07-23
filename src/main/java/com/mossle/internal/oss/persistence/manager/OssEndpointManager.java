package com.mossle.internal.oss.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.internal.oss.persistence.domain.OssEndpoint;

import org.springframework.stereotype.Service;

@Service
public class OssEndpointManager extends HibernateEntityDao<OssEndpoint> {
}
