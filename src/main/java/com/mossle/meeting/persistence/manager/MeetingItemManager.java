package com.mossle.meeting.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.meeting.persistence.domain.MeetingItem;

import org.springframework.stereotype.Service;

@Service
public class MeetingItemManager extends HibernateEntityDao<MeetingItem> {
}
