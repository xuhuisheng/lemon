package com.mossle.portal.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.portal.persistence.domain.PortalItem;

import org.springframework.stereotype.Service;

@Service
public class PortalItemManager extends HibernateEntityDao<PortalItem> {
}
