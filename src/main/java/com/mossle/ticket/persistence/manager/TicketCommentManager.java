package com.mossle.ticket.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.ticket.persistence.domain.TicketComment;

import org.springframework.stereotype.Service;

@Service
public class TicketCommentManager extends HibernateEntityDao<TicketComment> {
}
