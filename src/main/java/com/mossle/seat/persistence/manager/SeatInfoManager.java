package com.mossle.seat.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.seat.persistence.domain.SeatInfo;

import org.springframework.stereotype.Service;

@Service
public class SeatInfoManager extends HibernateEntityDao<SeatInfo> {
}
