package com.mossle.auth.manager;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.auth.domain.RoleDef;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class RoleDefManager extends HibernateEntityDao<RoleDef> {
}
