package com.mossle.core.hibernate;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.hibernate.context.spi.CurrentSessionContext;

import org.springframework.orm.hibernate4.SessionHolder;

import org.springframework.transaction.support.TransactionSynchronizationManager;

@SuppressWarnings("deprecation")
public class SpringSessionContext implements CurrentSessionContext {
    private final SessionFactory sessionFactory;

    public SpringSessionContext(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session currentSession() throws HibernateException {
        Object value = TransactionSynchronizationManager
                .getResource(this.sessionFactory);

        if (value instanceof Session) {
            return (Session) value;
        } else if (value instanceof SessionHolder) {
            SessionHolder sessionHolder = (SessionHolder) value;
            Session session = sessionHolder.getSession();

            if (TransactionSynchronizationManager.isSynchronizationActive()
                    && !sessionHolder.isSynchronizedWithTransaction()) {
                TransactionSynchronizationManager
                        .registerSynchronization(new SpringSessionSynchronization(
                                sessionHolder, this.sessionFactory));
                sessionHolder.setSynchronizedWithTransaction(true);

                // Switch to FlushMode.AUTO, as we have to assume a thread-bound Session
                // with FlushMode.MANUAL, which needs to allow flushing within the transaction.
                FlushMode flushMode = session.getFlushMode();

                if (FlushMode.isManualFlushMode(flushMode)
                        && !TransactionSynchronizationManager
                                .isCurrentTransactionReadOnly()) {
                    session.setFlushMode(FlushMode.AUTO);
                    sessionHolder.setPreviousFlushMode(flushMode);
                }
            }

            return session;
        } else {
            throw new HibernateException("No Session found for current thread");
        }
    }
}
