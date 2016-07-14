package com.mossle.attendance.persistence.manager;

import com.mossle.attendance.persistence.domain.AttendanceInfo;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class AttendanceInfoManager extends HibernateEntityDao<AttendanceInfo> {
}
