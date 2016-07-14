package com.mossle.internal.whitelist.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.internal.whitelist.persistence.domain.WhitelistIp;

import org.springframework.stereotype.Service;

@Service
public class WhitelistIpManager extends HibernateEntityDao<WhitelistIp> {
}
