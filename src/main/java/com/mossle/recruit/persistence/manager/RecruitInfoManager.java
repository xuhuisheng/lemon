package com.mossle.recruit.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.recruit.persistence.domain.RecruitInfo;

import org.springframework.stereotype.Service;

@Service
public class RecruitInfoManager extends HibernateEntityDao<RecruitInfo> {
}
