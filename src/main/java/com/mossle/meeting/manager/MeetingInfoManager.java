package com.mossle.meeting.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.meeting.domain.MeetingInfo;

import org.springframework.stereotype.Service;

@Service
public class MeetingInfoManager extends HibernateEntityDao<MeetingInfo> {
}
