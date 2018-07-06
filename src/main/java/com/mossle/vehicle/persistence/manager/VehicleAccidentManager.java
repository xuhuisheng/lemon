package com.mossle.vehicle.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.vehicle.persistence.domain.VehicleAccident;

import org.springframework.stereotype.Service;

@Service
public class VehicleAccidentManager extends HibernateEntityDao<VehicleAccident> {
}
