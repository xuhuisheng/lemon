package com.mossle.msg.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.msg.persistence.domain.MsgInfo;

import org.springframework.stereotype.Service;

@Service
public class MsgInfoManager extends HibernateEntityDao<MsgInfo> {
}
