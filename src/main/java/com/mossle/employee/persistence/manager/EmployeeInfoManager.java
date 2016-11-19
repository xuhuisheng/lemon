package com.mossle.employee.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.employee.persistence.domain.EmployeeInfo;

import org.springframework.stereotype.Service;

@Service
public class EmployeeInfoManager extends HibernateEntityDao<EmployeeInfo> {
}
