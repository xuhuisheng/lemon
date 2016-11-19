package com.mossle.user.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.user.persistence.domain.AccountDevice;

import org.springframework.stereotype.Service;

@Service
public class AccountDeviceManager extends HibernateEntityDao<AccountDevice> {
}
