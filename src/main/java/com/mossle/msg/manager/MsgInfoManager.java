package com.mossle.msg.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.msg.domain.MsgInfo;

import org.springframework.stereotype.Service;

@Service
public class MsgInfoManager extends HibernateEntityDao<MsgInfo> {
}
