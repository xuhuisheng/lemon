package com.mossle.vehicle.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.vehicle.persistence.domain.VehicleRequest;

import org.springframework.stereotype.Service;

@Service
public class VehicleRequestManager extends HibernateEntityDao<VehicleRequest> {
}
