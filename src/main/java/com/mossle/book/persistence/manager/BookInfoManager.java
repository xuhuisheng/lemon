package com.mossle.book.persistence.manager;

import com.mossle.book.persistence.domain.BookInfo;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class BookInfoManager extends HibernateEntityDao<BookInfo> {
}
