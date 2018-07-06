package com.mossle.vehicle.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.vehicle.persistence.domain.VehicleTask;

import org.springframework.stereotype.Service;

@Service
public class VehicleTaskManager extends HibernateEntityDao<VehicleTask> {
}
