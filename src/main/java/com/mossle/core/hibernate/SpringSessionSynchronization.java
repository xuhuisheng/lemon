package com.mossle.core.hibernate;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.Ordered;

import org.springframework.dao.DataAccessException;

import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.orm.hibernate4.SessionHolder;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SuppressWarnings("deprecation")
class SpringSessionSynchronization implements TransactionSynchronization,
        Ordered {
    private static Logger logger = LoggerFactory
            .getLogger(SpringSessionSynchronization.class);
    private final SessionHolder sessionHolder;
    private final SessionFactory sessionFactory;

    public SpringSessionSynchronization(SessionHolder sessionHolder,
            SessionFactory sessionFactory) {
        this.sessionHolder = sessionHolder;
        this.sessionFactory = sessionFactory;
    }

    private Session getCurrentSession() {
        return this.sessionHolder.getSession();
    }

    public int getOrder() {
        return SessionFactoryUtils.SESSION_SYNCHRONIZATION_ORDER;
    }

    public void suspend() {
        TransactionSynchronizationManager.unbindResource(this.sessionFactory);
        // Eagerly disconnect the Session here, to make release mode "on_close" work on JBoss.
        getCurrentSession().disconnect();
    }

    public void resume() {
        TransactionSynchronizationManager.bindResource(this.sessionFactory,
                this.sessionHolder);
    }

    public void flush() {
        try {
            logger.debug("Flushing Hibernate Session on explicit request");
            getCurrentSession().flush();
        } catch (HibernateException ex) {
            throw SessionFactoryUtils.convertHibernateAccessException(ex);
        }
    }

    public void beforeCommit(boolean readOnly) throws DataAccessException {
        if (!readOnly) {
            Session session = getCurrentSession();

            // Read-write transaction -> flush the Hibernate Session.
            // Further check: only flush when not FlushMode.MANUAL.
            if (!FlushMode.isManualFlushMode(session.getFlushMode())) {
                try {
                    logger.debug("Flushing Hibernate Session on transaction synchronization");
                    session.flush();
                } catch (HibernateException ex) {
                    throw SessionFactoryUtils
                            .convertHibernateAccessException(ex);
                }
            }
        }
    }

    public void beforeCompletion() {
        Session session = this.sessionHolder.getSession();

        if (this.sessionHolder.getPreviousFlushMode() != null) {
            // In case of pre-bound Session, restore previous flush mode.
            session.setFlushMode(this.sessionHolder.getPreviousFlushMode());
        }

        // Eagerly disconnect the Session here, to make release mode "on_close" work nicely.
        session.disconnect();
    }

    public void afterCommit() {
    }

    public void afterCompletion(int status) {
        try {
            if (status != STATUS_COMMITTED) {
                // Clear all pending inserts/updates/deletes in the Session.
                // Necessary for pre-bound Sessions, to avoid inconsistent state.
                this.sessionHolder.getSession().clear();
            }
        } finally {
            this.sessionHolder.setSynchronizedWithTransaction(false);
        }
    }
}
