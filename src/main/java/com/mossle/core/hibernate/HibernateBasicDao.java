package com.mossle.core.hibernate;

import java.io.Serializable;

import java.lang.reflect.InvocationTargetException;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import javax.transaction.Synchronization;

import com.mossle.core.id.IdGenerator;
import com.mossle.core.util.BeanUtils;
import com.mossle.core.util.ReflectUtils;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.hibernate.criterion.Order;

import org.hibernate.metadata.ClassMetadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeansException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 封装session和jdbcTemplate的基础类，不涉及泛型和CRUD操作.
 * 
 * @author Lingo
 */
@Transactional(rollbackFor = Exception.class)
public class HibernateBasicDao implements ApplicationContextAware {
    /** logger. */
    private static Logger logger = LoggerFactory
            .getLogger(HibernateBasicDao.class);

    /** application context. */
    private ApplicationContext applicationContext;

    /** sessionFactory. */
    private SessionFactory sessionFactory;

    /** jdbcTemplate. */
    private JdbcTemplate jdbcTemplate;

    /** idGenerator. */
    private IdGenerator idGenerator;

    /** default constructor. */
    public HibernateBasicDao() {
    }

    /**
     * constructor.
     * 
     * @param sessionFactory
     *            SessionFactory
     */
    public HibernateBasicDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /** @return SessionFactory. */
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /** @return jdbcTemplate. */
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    /** @return idGenerator. */
    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    /** @return session. */
    public Session getSession() {
        return this.sessionFactory.getCurrentSession();
    }

    // ============================================================================================
    // autowired
    // ============================================================================================
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        logger.debug("Autowired applicationContext");
        this.applicationContext = applicationContext;
    }

    /**
     * @param sessionFactory
     *            SessionFactory.
     */
    @Resource
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * @param jdbcTemplate
     *            JdbcTemplate.
     */
    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * @param idGenerator
     *            IdGenerator.
     */
    @Resource
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    // ============================================================================================
    // get, load, getAll, save, remove, removeById, removeAll
    // ============================================================================================

    /**
     * 获得一个实体类型的一条记录.
     * 
     * @param <T>
     *            实体类型
     * @param entityClass
     *            实体类型
     * @param id
     *            主键
     * @return 实例
     */
    @Transactional(readOnly = true)
    public <T> T get(Class<T> entityClass, Serializable id) {
        Assert.notNull(id, "Id can not be null.");

        return (T) this.getSession().get(entityClass, id);
    }

    /**
     * load一个实例，如果id不存在，会返回一个proxy，在调用proxy的时候出现问题. 使用这个方法，可以利用缓存，但是如果实例不存在，会出现不容易预计的错误
     * 
     * @param <T>
     *            实体类型
     * @param entityClass
     *            实体类型
     * @param id
     *            主键
     * @return 实例
     */
    @Transactional(readOnly = true)
    public <T> T load(Class<T> entityClass, Serializable id) {
        Assert.notNull(id, "Id can not be null.");

        return (T) this.getSession().load(entityClass, id);
    }

    /**
     * 获得一个实体类型的所有记录.
     * 
     * @param <T>
     *            实体类型
     * @param entityClass
     *            实体类型
     * @return 所有实例列表
     */
    @Transactional(readOnly = true)
    public <T> List<T> getAll(Class<T> entityClass) {
        return this.getSession().createCriteria(entityClass).list();
    }

    /**
     * 获得所有记录，带排序参数.
     * 
     * @param <T>
     *            实体类型
     * @param entityClass
     *            实体类型
     * @param orderBy
     *            排序字段名
     * @param isAsc
     *            是否正序排列
     * @return 返回结果列表
     */
    @Transactional(readOnly = true)
    public <T> List<T> getAll(Class<T> entityClass, String orderBy,
            boolean isAsc) {
        if (StringUtils.hasText(orderBy)) {
            Criteria criteria = this.getSession().createCriteria(entityClass);

            if (isAsc) {
                criteria.addOrder(Order.asc(orderBy));
            } else {
                criteria.addOrder(Order.desc(orderBy));
            }

            return criteria.list();
        } else {
            return this.getAll(entityClass);
        }
    }

    /**
     * 添加或更新. 相关的操作包括：save, update, saveOrUpdate, merge, persist, refresh
     * 
     * @param entity
     *            实例
     */
    @Transactional
    public void save(Object entity) {
        Assert.notNull(entity, "Entity can not be null.");
        this.getSession().saveOrUpdate(entity);
        logger.debug("save entity: {}", entity);
    }

    @Transactional
    public void insert(Object entity) {
        Assert.notNull(entity, "Entity can not be null.");
        this.getSession().save(entity);
        logger.debug("insert entity: {}", entity);
    }

    @Transactional
    public void update(Object entity) {
        Assert.notNull(entity, "Entity can not be null.");
        this.getSession().update(entity);
        logger.debug("update entity: {}", entity);
    }

    /**
     * 删除一条记录.
     * 
     * @param entity
     *            实例
     */
    @Transactional
    public void remove(Object entity) {
        Assert.notNull(entity, "Entity can not be null.");
        this.getSession().delete(entity);
        logger.debug("remove entity: {}", entity);
    }

    /**
     * 根据主键删除记录.
     * 
     * @param <T>
     *            实体类型
     * @param entityClass
     *            实体类型
     * @param id
     *            主键
     */
    @Transactional
    public <T> void removeById(Class<T> entityClass, Serializable id) {
        Assert.notNull(id, "Id can not be null.");
        this.remove(this.load(entityClass, id));
        logger.debug("remove entity by id: {}", id);
    }

    /**
     * 删除集合里的所有记录.
     * 
     * @param list
     *            需要删除的集合
     */
    @Transactional
    public void removeAll(Collection list) {
        Assert.notNull(list, "List can not be null.");

        for (Object obj : list) {
            this.remove(obj);
        }
    }

    /**
     * 删除所有记录.
     * 
     * @param <T>
     *            实体类型
     * @param entityClass
     *            实体类型
     */
    @Transactional
    public <T> void removeAll(Class<T> entityClass) {
        this.removeAll(this.getAll(entityClass));
    }

    // ============================================================================================
    // flush, clear, evict, initialize
    // ============================================================================================
    /** 把session中的数据flush到数据库里. */
    public void flush() {
        this.getSession().flush();
    }

    /** 清空session. */
    public void clear() {
        this.getSession().clear();
    }

    /**
     * 把实体类对象从session中删除.
     * 
     * @param entity
     *            实体类
     */
    public void evict(Object entity) {
        Assert.notNull(entity, "Entity cannot be null");
        this.getSession().evict(entity);
    }

    /**
     * 直接初始化数据，避免出现lazy load错误的一个方法.
     * 
     * @param object
     *            entity
     */
    public void initialize(Object object) {
        Assert.notNull(object, "Object cannot be null");
        Hibernate.initialize(object);
    }

    // ============================================================================================
    // getId, getIdName
    // ============================================================================================
    /**
     * 取得对象的主键值，辅助函数.
     * 
     * @param entityClass
     *            实体类型
     * @param entity
     *            实体对象
     * @return 主键
     * @throws NoSuchMethodException
     *             找不到方法
     * @throws IllegalAccessException
     *             没有访问权限
     * @throws InvocationTargetException
     *             反射异常
     */
    public Serializable getId(Class entityClass, Object entity)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        Assert.notNull(entity);

        String idName = getIdName(entityClass);
        String getterName = ReflectUtils.getGetterMethodName(entity, idName);

        return (Serializable) BeanUtils.invokeMethod(entity, getterName);
    }

    public void setId(Class entityClass, Object entity, Serializable idValue) {
        Assert.notNull(entity);
        Assert.notNull(idValue);

        try {
            String idName = getIdName(entityClass);
            String setterName = ReflectUtils.getSetterMethodName(idName);
            BeanUtils.invokeMethod(entity, setterName, idValue);
        } catch (NoSuchMethodException ex) {
            logger.info(ex.getMessage(), ex);
        } catch (IllegalAccessException ex) {
            logger.info(ex.getMessage(), ex);
        } catch (InvocationTargetException ex) {
            logger.info(ex.getMessage(), ex);
        }
    }

    /**
     * 取得对象的主键名,辅助函数.
     * 
     * @param entityClass
     *            实体类型
     * @return 主键名称
     */
    public String getIdName(Class entityClass) {
        Assert.notNull(entityClass);
        entityClass = ReflectUtils.getOriginalClass(entityClass);

        ClassMetadata meta = this.getSessionFactory().getClassMetadata(
                entityClass);
        Assert.notNull(meta, "Class " + entityClass
                + " not define in hibernate session factory.");

        String idName = meta.getIdentifierPropertyName();
        Assert.hasText(idName, entityClass.getSimpleName()
                + " has no identifier property define.");

        return idName;
    }

    // ============================================================================================
    // publish event
    // ============================================================================================
    public void registerSynchronization(Synchronization synchronization) {
        SynchronizationNotification synchronizationNotification = new SynchronizationNotification(
                synchronization);
        TransactionSynchronizationManager
                .registerSynchronization(synchronizationNotification);
    }

    public void publishEvent(ApplicationEvent applicationEvent) {
        this.applicationContext.publishEvent(applicationEvent);
    }

    public static class SynchronizationNotification extends
            TransactionSynchronizationAdapter {
        private Synchronization synchronization;

        public SynchronizationNotification(Synchronization synchronization) {
            this.synchronization = synchronization;
        }

        public void afterCompletion(int status) {
            synchronization.afterCompletion(status);
        }

        public void beforeCompletion() {
            synchronization.beforeCompletion();
        }
    }
}
