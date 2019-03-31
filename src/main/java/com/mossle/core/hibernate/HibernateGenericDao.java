package com.mossle.core.hibernate;

import java.io.Serializable;

import java.lang.reflect.InvocationTargetException;

import java.util.List;
import java.util.Map;

import com.mossle.core.util.BeanUtils;
import com.mossle.core.util.ReflectUtils;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 提供基础功能方法的hibernate基类.
 * 
 * @author Lingo
 */
public class HibernateGenericDao extends HibernateBasicDao {
    /** default constructor. */
    public HibernateGenericDao() {
    }

    /**
     * constructor.
     * 
     * @param sessionFactory
     *            SessionFactory
     */
    public HibernateGenericDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    // ============================================================================================
    // createQuery
    // ============================================================================================

    /**
     * 生成一个Query.
     * 
     * @param hql
     *            HQL语句
     * @param values
     *            参数
     * @return Query
     */
    public Query createQuery(String hql, Object... values) {
        Assert.hasText(hql, "hql cannot be null");

        Query query = this.getSession().createQuery(hql);

        for (int i = 0; i < values.length; i++) {
            query.setParameter(i, values[i]);
        }

        return query;
    }

    /**
     * create query by hql and map.
     * 
     * @param hql
     *            String
     * @param map
     *            Map
     * @return Query
     */
    public Query createQuery(String hql, Map<String, Object> map) {
        Assert.hasText(hql, "hql cannot be null");

        Query query = this.getSession().createQuery(hql);

        if (map != null) {
            query.setProperties(map);
        }

        return query;
    }

    // ============================================================================================
    // createCriteria
    // ============================================================================================
    /**
     * 根据entityClass生成对应类型的Criteria.
     * 
     * @param entityClass
     *            实体类型
     * @param criterions
     *            条件
     * @return Criteria
     */
    public Criteria createCriteria(Class entityClass, Criterion... criterions) {
        Criteria criteria = this.getSession().createCriteria(entityClass);

        for (Criterion c : criterions) {
            criteria.add(c);
        }

        return criteria;
    }

    /**
     * 根据entityClass，生成带排序的Criteria.
     * 
     * @param <T>
     *            实体类型
     * @param entityClass
     *            类型
     * @param orderBy
     *            排序字段名
     * @param isAsc
     *            是否正序
     * @param criterions
     *            条件
     * @return Criteria
     */
    public <T> Criteria createCriteria(Class<T> entityClass, String orderBy,
            boolean isAsc, Criterion... criterions) {
        if (StringUtils.hasText(orderBy)) {
            Criteria criteria = createCriteria(entityClass, criterions);

            if (isAsc) {
                criteria.addOrder(Order.asc(orderBy));
            } else {
                criteria.addOrder(Order.desc(orderBy));
            }

            return criteria;
        } else {
            return createCriteria(entityClass, criterions);
        }
    }

    // ============================================================================================
    // find Criteria
    // ============================================================================================
    /**
     * find by critrions.
     * 
     * @param entityClass
     *            Class
     * @param criterions
     *            Criterion...
     * @param <T>
     *            generic
     * @return List
     */
    @Transactional(readOnly = true)
    public <T> List<T> find(Class<T> entityClass, Criterion... criterions) {
        return this.createCriteria(entityClass, criterions).list();
    }

    /**
     * find unique by critrions.
     * 
     * @param entityClass
     *            Class
     * @param criterions
     *            Criterion...
     * @param <T>
     *            generic
     * @return T
     */
    @Transactional(readOnly = true)
    public <T> T findUnique(Class<T> entityClass, Criterion... criterions) {
        return (T) createCriteria(entityClass, criterions).uniqueResult();
    }

    // ============================================================================================
    // find Query
    // ============================================================================================
    /**
     * find by hql and values.
     * 
     * @param hql
     *            String
     * @param values
     *            Object
     * @return List
     */
    @Transactional(readOnly = true)
    public List find(String hql, Object... values) {
        return this.createQuery(hql, values).list();
    }

    /**
     * find by hql and map.
     * 
     * @param hql
     *            String
     * @param map
     *            Map
     * @return List
     */
    @Transactional(readOnly = true)
    public List find(String hql, Map<String, Object> map) {
        return this.createQuery(hql, map).list();
    }

    /**
     * 查询唯一记录.
     * 
     * @param <T>
     *            实体类型
     * @param hql
     *            HQL字符串
     * @param values
     *            参数
     * @return 实例
     */
    @Transactional(readOnly = true)
    public <T> T findUnique(String hql, Object... values) {
        return (T) this.createQuery(hql, values).setMaxResults(1)
                .uniqueResult();
    }

    /**
     * 查询唯一记录.
     * 
     * @param <T>
     *            实体类型
     * @param hql
     *            HQL字符串
     * @param map
     *            Map
     * @return 实例
     */
    @Transactional(readOnly = true)
    public <T> T findUnique(String hql, Map<String, Object> map) {
        return (T) this.createQuery(hql, map).setMaxResults(1).uniqueResult();
    }

    // ============================================================================================
    // findBy
    // ============================================================================================
    /**
     * 根据name，value进行查询.
     * 
     * @param <T>
     *            实体类型
     * @param entityClass
     *            实体类型
     * @param name
     *            字段名
     * @param value
     *            参数值
     * @return 查询结果
     */
    @Transactional(readOnly = true)
    public <T> List<T> findBy(Class<T> entityClass, String name, Object value) {
        Assert.hasText(name, "property name cannot be null");

        return this.createCriteria(entityClass, Restrictions.eq(name, value))
                .list();
    }

    /**
     * 根据name,value进行模糊查询.
     * 
     * @param <T>
     *            实体类型
     * @param entityClass
     *            实体类型
     * @param name
     *            字段名
     * @param value
     *            用来做模糊查询的字段值
     * @return 查询结果
     */
    @Transactional(readOnly = true)
    public <T> List<T> findByLike(Class<T> entityClass, String name,
            Object value) {
        Assert.hasText(name, "property name cannot be null");

        return this.createCriteria(entityClass, Restrictions.like(name, value))
                .list();
    }

    /**
     * find by ids.
     * 
     * @param entityClass
     *            Class
     * @param ids
     *            List
     * @param <T>
     *            generic
     * @return List
     */
    @Transactional(readOnly = true)
    public <T> List<T> findByIds(Class<T> entityClass, List ids) {
        Assert.notEmpty(ids);

        String idName = this.getIdName(entityClass);
        Criterion criterion = Restrictions.in(idName, ids);

        return this.find(entityClass, criterion);
    }

    /**
     * 查询唯一记录.
     * 
     * @param <T>
     *            实体类型
     * @param entityClass
     *            实体类型
     * @param name
     *            字段名
     * @param value
     *            字段值
     * @return 实例
     */
    @Transactional(readOnly = true)
    public <T> T findUniqueBy(Class<T> entityClass, String name, Object value) {
        return (T) this
                .createCriteria(entityClass, Restrictions.eq(name, value))
                .setMaxResults(1).uniqueResult();
    }

    // ============================================================================================
    // isUnique
    // ============================================================================================
    /**
     * 判断对象某些属性的值在数据库中是否唯一.
     * 
     * @param entityClass
     *            实体类型
     * @param entity
     *            实体对象
     * @param uniquePropertyNames
     *            在POJO里不能重复的属性列表,以逗号分割 如"name,loginid,password"
     * @param <T>
     *            实体类泛型
     * @return 如果唯一返回true，否则返回false
     * @throws NoSuchMethodException cannot find getter
     * @throws IllegalAccessException cannot access getter
     * @throws InvocationTargetException invoke getter error
     */
    @Transactional(readOnly = true)
    public <T> boolean isUnique(Class<T> entityClass, T entity,
            String uniquePropertyNames) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        Assert.hasText(uniquePropertyNames);

        Criteria criteria = createCriteria(entityClass).setProjection(
                Projections.rowCount());
        String[] nameList = uniquePropertyNames.split(",");

        // 循环加入唯一列
        for (String name : nameList) {
            String getterMethodName = ReflectUtils.getGetterMethodName(entity,
                    name);
            criteria.add(Restrictions.eq(name,
                    BeanUtils.invokeMethod(entity, getterMethodName)));
        }

        // 以下代码为了如果是update的情况,排除entity自身.
        String idName = getIdName(entityClass);

        // 取得entity的主键值
        Serializable id = getId(entityClass, entity);

        // 如果id!=null,说明对象已存在,该操作为update,加入排除自身的判断
        if (id != null) {
            criteria.add(Restrictions.not(Restrictions.eq(idName, id)));
        }

        Object result = criteria.uniqueResult();

        return HibernateUtils.getNumber(result) == 0;
    }

    // ============================================================================================
    // getCount
    // ============================================================================================
    /**
     * 获得总记录数.
     * 
     * @param <T>
     *            实体类型
     * @param entityClass
     *            实体类型
     * @return 总数
     */
    @Transactional(readOnly = true)
    public <T> Integer getCount(Class<T> entityClass) {
        return this.getCount(this.createCriteria(entityClass));
    }

    /**
     * 获得总记录数.
     * 
     * @param criteria
     *            条件
     * @return 总数
     */
    @Transactional(readOnly = true)
    public Integer getCount(Criteria criteria) {
        Object result = criteria.setProjection(Projections.rowCount())
                .uniqueResult();

        return HibernateUtils.getNumber(result);
    }

    /**
     * 获得总记录数.
     * 
     * @param hql
     *            HQL字符串
     * @param values
     *            参数
     * @return 总数
     */
    @Transactional(readOnly = true)
    public Integer getCount(String hql, Object... values) {
        Object result = createQuery(hql, values).uniqueResult();

        return HibernateUtils.getNumber(result);
    }

    /**
     * 获得总记录数.
     * 
     * @param hql
     *            HQL字符串
     * @param map
     *            Map
     * @return 总数
     */
    @Transactional(readOnly = true)
    public Integer getCount(String hql, Map<String, Object> map) {
        Object result = createQuery(hql, map).uniqueResult();

        return HibernateUtils.getNumber(result);
    }

    // ============================================================================================
    // batchUpdate
    // ============================================================================================
    /**
     * batch update by hql and objects.
     * 
     * @param hql
     *            String
     * @param values
     *            Object
     * @return int
     */
    @Transactional
    public int batchUpdate(String hql, Object... values) {
        return this.createQuery(hql, values).executeUpdate();
    }

    /**
     * batch update by hql and map.
     * 
     * @param hql
     *            String
     * @param map
     *            Map
     * @return int
     */
    @Transactional
    public int batchUpdate(String hql, Map<String, Object> map) {
        return this.createQuery(hql, map).executeUpdate();
    }
}
