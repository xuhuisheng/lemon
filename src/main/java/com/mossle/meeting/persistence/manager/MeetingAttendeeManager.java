package com.mossle.meeting.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.meeting.persistence.domain.MeetingAttendee;

import org.springframework.stereotype.Service;

@Service
public class MeetingAttendeeManager extends HibernateEntityDao<MeetingAttendee> {
}
