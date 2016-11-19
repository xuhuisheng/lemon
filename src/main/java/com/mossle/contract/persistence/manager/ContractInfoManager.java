package com.mossle.contract.persistence.manager;

import com.mossle.contract.persistence.domain.ContractInfo;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class ContractInfoManager extends HibernateEntityDao<ContractInfo> {
}
