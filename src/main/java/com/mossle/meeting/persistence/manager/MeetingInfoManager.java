package com.mossle.meeting.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.meeting.persistence.domain.MeetingInfo;

import org.springframework.stereotype.Service;

@Service
public class MeetingInfoManager extends HibernateEntityDao<MeetingInfo> {
}
