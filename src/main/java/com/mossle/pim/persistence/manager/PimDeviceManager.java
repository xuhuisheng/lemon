package com.mossle.pim.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.pim.persistence.domain.PimDevice;

import org.springframework.stereotype.Service;

@Service
public class PimDeviceManager extends HibernateEntityDao<PimDevice> {
}
