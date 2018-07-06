package com.mossle.vehicle.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.vehicle.persistence.domain.VehicleInfo;

import org.springframework.stereotype.Service;

@Service
public class VehicleInfoManager extends HibernateEntityDao<VehicleInfo> {
}
