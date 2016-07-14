package com.mossle.meeting.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.meeting.persistence.domain.MeetingRoom;

import org.springframework.stereotype.Service;

@Service
public class MeetingRoomManager extends HibernateEntityDao<MeetingRoom> {
}
