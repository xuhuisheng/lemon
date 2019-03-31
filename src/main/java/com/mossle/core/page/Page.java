package com.mossle.core.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * 分页参数与分页结果.
 * 
 * @author Lingo
 */
public class Page {
    // ==========================================
    // static fields...
    /** 正序. */
    public static final String ASC = "ASC";

    /** 倒序. */
    public static final String DESC = "DESC";

    /** 默认每页显示10条数据. */
    public static final int DEFAULT_PAGE_SIZE = 10;

    // ==========================================
    // fields...
    /** 当前第几页，默认值为1，既第一页. */
    private int pageNo = 1;

    /** 每页最大记录数，默认值为10. */
    private int pageSize = DEFAULT_PAGE_SIZE;

    /** 排序字段名. */
    private List<String> orderBys = new ArrayList<String>();

    /** 使用正序还是倒序. */
    private List<String> orders = new ArrayList<String>();

    /** 查询结果. */
    private Object result;

    /** 总记录数，默认值为-1，表示totalCount不可用. */
    private long totalCount = -1L;

    /** 是否计算数据库中的记录总数. */
    private boolean autoCount;

    /** 当前页第一条记录的索引，默认值为0，既第一页第一条记录. */
    private long start;

    /** 总页数，默认值为-1，表示pageCount不可用. */
    private long pageCount = -1;

    // ==========================================
    // constructor...
    /** 构造方法. */
    public Page() {
        totalCount = 0;
        result = new ArrayList();
    }

    /**
     * 构造方法.
     * 
     * @param result
     *            Object
     * @param totalCount
     *            int
     */
    public Page(Object result, int totalCount) {
        this.result = result;
        this.totalCount = totalCount;
    }

    /**
     * 构造方法.
     * 
     * @param pageNo
     *            int
     * @param pageSize
     *            int
     * @param orderBy
     *            String
     * @param order
     *            String
     */
    public Page(int pageNo, int pageSize, String orderBy, String order) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.setOrderBy(orderBy);
        this.checkAndSetOrder(order);
        this.calculateStart();
    }

    // ==========================================
    // 工具方法
    /**
     * 是否为正序排序.
     * 
     * @return boolean
     */
    public boolean isAsc() {
        return !DESC.equalsIgnoreCase(this.getOrder());
    }

    /**
     * 取得倒转的排序方向.
     * 
     * @return 如果dir=='ASC'就返回'DESC'，如果dir='DESC'就返回'ASC'
     */
    public String getInverseOrder() {
        if (DESC.equalsIgnoreCase(this.getOrder())) {
            return ASC;
        } else {
            return DESC;
        }
    }

    /**
     * 页面显示最大记录数是否可用.
     * 
     * @return pageSize > 0
     */
    public boolean isPageSizeEnabled() {
        return pageSize > 0;
    }

    /**
     * 页面第一条记录的索引是否可用.
     * 
     * @return start
     */
    public boolean isStartEnabled() {
        return start >= 0;
    }

    /**
     * 排序是否可用.
     * 
     * @return orderBy是否非空
     */
    public boolean isOrderEnabled() {
        return ((!orderBys.isEmpty()) && (!orders.isEmpty()));
    }

    /**
     * 是否有前一页.
     * 
     * @return boolean
     */
    public boolean isPreviousEnabled() {
        return pageNo > 1;
    }

    /**
     * 是否有后一页.
     * 
     * @return boolean
     */
    public boolean isNextEnabled() {
        return pageNo < pageCount;
    }

    /**
     * 总页数是否可用.
     * 
     * @return boolean
     */
    public boolean isPageCountEnabled() {
        return pageCount >= 0;
    }

    /** 计算本页第一条记录的索引. */
    private void calculateStart() {
        if ((pageNo < 1) || (pageSize < 1)) {
            start = -1;
        } else {
            start = (pageNo - 1L) * pageSize;
        }
    }

    /** 计算最大页数. */
    private void calculatePageCount() {
        if ((totalCount < 0) || (pageSize < 1)) {
            pageCount = -1;
        } else {
            pageCount = ((totalCount - 1) / pageSize) + 1;
        }
    }

    // ==========================================
    // getter and setter method...
    /** @return pageNo. */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * @param pageNo
     *            int.
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
        this.calculateStart();
    }

    /** @return pageSize. */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize
     *            int.
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        this.calculateStart();
        this.calculatePageCount();
    }

    /** @return orderBy. */
    public String getOrderBy() {
        if (!this.orderBys.isEmpty()) {
            return this.orderBys.get(0);
        }

        return null;
    }

    /**
     * @param orderBy
     *            String.
     */
    public void setOrderBy(String orderBy) {
        if ((orderBy == null) || (orderBy.trim().length() == 0)) {
            throw new IllegalArgumentException("orderBy should be blank");
        }

        this.orderBys.clear();
        this.orderBys.add(orderBy);

        if (this.getOrders().size() != 1) {
            this.setOrder(ASC);
        }
    }

    /** @return order. */
    public String getOrder() {
        if (!this.orders.isEmpty()) {
            return this.orders.get(0);
        }

        return ASC;
    }

    /**
     * @param order
     *            String.
     */
    public void setOrder(String order) {
        this.checkAndSetOrder(order);
    }

    /** @return result. */
    public Object getResult() {
        return result;
    }

    /**
     * @param result
     *            Object.
     */
    public void setResult(Object result) {
        this.result = result;
    }

    /** @return totalCount. */
    public long getTotalCount() {
        return totalCount;
    }

    /**
     * @param totalCount
     *            int.
     */
    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
        this.calculatePageCount();
    }

    /** @return autoCount. */
    public boolean isAutoCount() {
        return autoCount;
    }

    /**
     * @param autoCount
     *            boolean.
     */
    public void setAutoCount(boolean autoCount) {
        this.autoCount = autoCount;
    }

    /** @return start. */
    public long getStart() {
        return start;
    }

    /** @return pageCount. */
    public long getPageCount() {
        return pageCount;
    }

    /** @return result size. */
    public int getResultSize() {
        if (result instanceof Collection) {
            return ((Collection) result).size();
        } else {
            return 0;
        }
    }

    /**
     * chck and set order.
     * 
     * @param text
     *            String
     */
    private void checkAndSetOrder(String text) {
        if (ASC.equalsIgnoreCase(text) || DESC.equalsIgnoreCase(text)) {
            text = text.toUpperCase(Locale.CHINA);
            this.orders.clear();
            this.orders.add(text);
        } else {
            throw new IllegalArgumentException(
                    "order should be 'DESC' or 'ASC'");
        }
    }

    public void setDefaultOrder(String orderBy, String order) {
        if (!this.isOrderEnabled()) {
            this.setOrderBy(orderBy);
            this.setOrder(order);
        }
    }

    public void addOrder(String orderBy, String order) {
        if (this.orderBys.size() != this.orders.size()) {
            this.orderBys.clear();
            this.orders.clear();
        }

        this.orderBys.add(orderBy);

        if (ASC.equalsIgnoreCase(order) || DESC.equalsIgnoreCase(order)) {
            order = order.toUpperCase(Locale.CHINA);
            this.orders.add(order);
        } else {
            throw new IllegalArgumentException(
                    "order should be 'DESC' or 'ASC'");
        }
    }

    public List<String> getOrderBys() {
        return this.orderBys;
    }

    public void setOrderBys(List<String> orderBys) {
        this.orderBys = orderBys;
    }

    public List<String> getOrders() {
        return this.orders;
    }

    public void setOrders(List<String> orders) {
        this.orders = orders;
    }
}
