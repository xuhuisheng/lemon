package com.mossle.ticket.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.ticket.persistence.domain.TicketInfo;

import org.springframework.stereotype.Service;

@Service
public class TicketInfoManager extends HibernateEntityDao<TicketInfo> {
}
