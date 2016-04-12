package com.mossle.book.persistence.manager;

import com.mossle.book.persistence.domain.BookBorrow;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class BookBorrowManager extends HibernateEntityDao<BookBorrow> {
}
