package com.mossle.ticket.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.ticket.persistence.domain.TicketGroup;

import org.springframework.stereotype.Service;

@Service
public class TicketGroupManager extends HibernateEntityDao<TicketGroup> {
}
