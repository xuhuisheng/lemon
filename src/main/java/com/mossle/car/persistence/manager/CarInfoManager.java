package com.mossle.car.persistence.manager;

import com.mossle.car.persistence.domain.CarInfo;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class CarInfoManager extends HibernateEntityDao<CarInfo> {
}
