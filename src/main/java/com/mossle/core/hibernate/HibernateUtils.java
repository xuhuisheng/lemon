package com.mossle.core.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mossle.core.util.BeanUtils;

import org.hibernate.Criteria;
import org.hibernate.Query;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Restrictions;

import org.hibernate.internal.CriteriaImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;

/**
 * hibernate utils.
 * 
 * @author Lingo
 */
public class HibernateUtils {
    /** logger. */
    private static Logger logger = LoggerFactory
            .getLogger(HibernateUtils.class);

    /** protected constructor. */
    protected HibernateUtils() {
    }

    /**
     * get number for count.
     * 
     * @param result
     *            Object
     * @return Integer
     */
    public static Integer getNumber(Object result) {
        if (result == null) {
            return 0;
        } else {
            return ((Number) result).intValue();
        }
    }

    /**
     * 去除hql的select 子句，未考虑union的情况,用于pagedQuery.
     * 
     * @param hql
     *            HQL字符串
     * @return 删除select语句后的字符串
     * @see HibernatePagingDao#pagedQuery(String,int,int,Object[])
     */
    public static String removeSelect(String hql) {
        Assert.hasText(hql);

        if (hql.toLowerCase(Locale.ENGLISH).indexOf("distinct") != -1) {
            logger.warn(
                    "there is a distinct in paged query hql : [{}], this maybe cause an unexpected result",
                    hql);
        }

        int beginPos = hql.toLowerCase(Locale.CHINA).indexOf("from");
        Assert.isTrue(beginPos != -1, " hql : " + hql
                + " must has a keyword 'from'");

        return hql.substring(beginPos);
    }

    /**
     * 去除hql的order by 子句，用于pagedQuery.
     * 
     * @param hql
     *            HQL字符串
     * @return 删除排序语句后的字符串
     * @see HibernatePagingDao#pagedQuery(String,int,int,Object[])
     */
    public static String removeOrders(String hql) {
        Assert.hasText(hql);

        Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*",
                Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(hql);
        StringBuffer sb = new StringBuffer();

        while (m.find()) {
            m.appendReplacement(sb, "");
        }

        m.appendTail(sb);

        return sb.toString();
    }

    /**
     * distinct.
     * 
     * @param query
     *            Query
     * @return Query
     */
    public static Query distinct(Query query) {
        query.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

        return query;
    }

    /**
     * distinct.
     * 
     * @param criteria
     *            Criteria
     * @return Criteria
     */
    public static Criteria distinct(Criteria criteria) {
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

        return criteria;
    }

    /**
     * find projection from criteria.
     * 
     * @param criteria
     *            Criteria
     * @return Projection
     */
    public static Projection findProjection(Criteria criteria) {
        if (criteria instanceof CriteriaImpl) {
            return ((CriteriaImpl) criteria).getProjection();
        } else {
            throw new IllegalArgumentException(criteria
                    + " is not a CriteriaImpl");
        }
    }

    /**
     * find order entries.
     * 
     * @param criteria
     *            Criteria
     * @return List
     */
    public static List findOrderEntries(Criteria criteria) {
        return (List) BeanUtils.safeGetFieldValue(criteria, "orderEntries");
    }

    /**
     * set order entries.
     * 
     * @param criteria
     *            Criteria
     * @param orderEntries
     *            List
     */
    public static void setOrderEntries(Criteria criteria, List orderEntries) {
        BeanUtils.safeSetFieldValue(criteria, "orderEntries", orderEntries);
    }

    /**
     * 按属性条件参数创建Criterion,辅助函数.
     * 
     * @param propertyName
     *            String
     * @param propertyValue
     *            Object
     * @param matchType
     *            MatchType
     * @return Criterion
     */
    public static Criterion buildCriterion(String propertyName,
            Object propertyValue, MatchType matchType) {
        Assert.hasText(propertyName, "propertyName不能为空");

        Criterion criterion = null;

        // 根据MatchType构造criterion
        switch (matchType) {
        case EQ:
            criterion = Restrictions.eq(propertyName, propertyValue);

            break;

        case LIKE:
            criterion = Restrictions.like(propertyName, (String) propertyValue,
                    MatchMode.ANYWHERE);

            break;

        case LE:
            criterion = Restrictions.le(propertyName, propertyValue);

            break;

        case LT:
            criterion = Restrictions.lt(propertyName, propertyValue);

            break;

        case GE:
            criterion = Restrictions.ge(propertyName, propertyValue);

            break;

        case GT:
            criterion = Restrictions.gt(propertyName, propertyValue);

            break;

        case IN:
            criterion = Restrictions.in(propertyName,
                    (Collection) propertyValue);

            break;

        default:
            criterion = Restrictions.eq(propertyName, propertyValue);

            break;
        }

        return criterion;
    }

    /**
     * 按属性条件列表创建Criterion数组,辅助函数.
     * 
     * @param filters
     *            List
     * @return Criterion[]
     */
    public static Criterion[] buildCriterion(List<PropertyFilter> filters) {
        List<Criterion> criterionList = new ArrayList<Criterion>();

        for (PropertyFilter filter : filters) {
            // 只有一个属性需要比较的情况.
            if (!filter.hasMultiProperties()) {
                Criterion criterion = buildCriterion(filter.getPropertyName(),
                        filter.getMatchValue(), filter.getMatchType());
                criterionList.add(criterion);
            } else {
                // 包含多个属性需要比较的情况,进行or处理.
                Disjunction disjunction = Restrictions.disjunction();

                for (String param : filter.getPropertyNames()) {
                    Criterion criterion = buildCriterion(param,
                            filter.getMatchValue(), filter.getMatchType());
                    disjunction.add(criterion);
                }

                criterionList.add(disjunction);
            }
        }

        return criterionList.toArray(new Criterion[criterionList.size()]);
    }

    public static void buildQuery(StringBuilder buff,
            PropertyFilter propertyFilter) {
        if (buff.toString().toLowerCase().indexOf("where") == -1) {
            buff.append(" where ");
        } else {
            buff.append(" and ");
        }

        buff.append(propertyFilter.getPropertyName());

        switch (propertyFilter.getMatchType()) {
        case EQ:
            buff.append(" =:");

            break;

        case LIKE:
            buff.append(" like:");

            break;

        case LE:
            buff.append(" <=:");

            break;

        case LT:
            buff.append(" <:");

            break;

        case GE:
            buff.append(" >=:");

            break;

        case GT:
            buff.append(" >:");

            break;

        case IN:
            buff.append(" in :");

            break;

        default:
            buff.append(" =:");

            break;
        }

        buff.append(propertyFilter.getPropertyName().replaceAll("\\.", "_"));
    }
}
