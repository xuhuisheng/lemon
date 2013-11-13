package com.mossle.meeting.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.meeting.domain.MeetingRoom;

import org.springframework.stereotype.Service;

@Service
public class MeetingRoomManager extends HibernateEntityDao<MeetingRoom> {
}
