package com.mossle.user.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.user.persistence.domain.UserRepo;

import org.springframework.stereotype.Service;

@Service
public class UserRepoManager extends HibernateEntityDao<UserRepo> {
}
