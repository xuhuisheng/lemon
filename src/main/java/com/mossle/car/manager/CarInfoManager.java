package com.mossle.car.manager;

import com.mossle.car.domain.CarInfo;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class CarInfoManager extends HibernateEntityDao<CarInfo> {
}
