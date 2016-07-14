package com.mossle.ticket.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.ticket.persistence.domain.TicketCatalog;

import org.springframework.stereotype.Service;

@Service
public class TicketCatalogManager extends HibernateEntityDao<TicketCatalog> {
}
