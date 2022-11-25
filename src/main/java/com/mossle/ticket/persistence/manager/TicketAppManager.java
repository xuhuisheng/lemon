package com.mossle.ticket.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.ticket.persistence.domain.TicketApp;

import org.springframework.stereotype.Service;

@Service
public class TicketAppManager extends HibernateEntityDao<TicketApp> {
}
