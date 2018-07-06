package com.mossle.vehicle.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.vehicle.persistence.domain.VehicleDriver;

import org.springframework.stereotype.Service;

@Service
public class VehicleDriverManager extends HibernateEntityDao<VehicleDriver> {
}
