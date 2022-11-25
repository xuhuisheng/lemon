package com.mossle.internal.open.persistence.domain;

// Generated by Hibernate Tools
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * SysInfo 系统信息.
 * 
 * @author Lingo
 */
@Entity
@Table(name = "SYS_INFO")
public class SysInfo implements java.io.Serializable {
    private static final long serialVersionUID = 0L;

    /** 主键. */
    private Long id;

    /** 外键，应用. */
    private OpenApp openApp;

    /** 外键，系统分类. */
    private SysCategory sysCategory;

    /** 图标. */
    private String logo;

    /** 分类. */
    private String type;

    /** 编码. */
    private String code;

    /** 名称. */
    private String name;

    /** 网址. */
    private String url;

    /** 排序. */
    private Integer priority;

    /** 状态. */
    private String status;

    /** 备注. */
    private String descn;

    /** 平台，web, app. */
    private String platform;

    /** 创建时间. */
    private Date createTime;

    /** 创建人. */
    private String userId;

    /** 账号体系. */
    private String userRepoCode;

    /** 租户. */
    private String tenantId;

    /** app key. */
    private String appKey;

    /** app secret. */
    private String appSecret;

    /** 移动端网址. */
    private String appUrl;

    /** PC端网址. */
    private String pcUrl;

    /** 管理端网址. */
    private String adminUrl;

    /** . */
    private Set<SysEntry> sysEntries = new HashSet<SysEntry>(0);

    public SysInfo() {
    }

    public SysInfo(Long id) {
        this.id = id;
    }

    public SysInfo(Long id, OpenApp openApp, SysCategory sysCategory,
            String logo, String type, String code, String name, String url,
            Integer priority, String status, String descn, String platform,
            Date createTime, String userId, String userRepoCode,
            String tenantId, String appKey, String appSecret, String appUrl,
            String pcUrl, String adminUrl, Set<SysEntry> sysEntries) {
        this.id = id;
        this.openApp = openApp;
        this.sysCategory = sysCategory;
        this.logo = logo;
        this.type = type;
        this.code = code;
        this.name = name;
        this.url = url;
        this.priority = priority;
        this.status = status;
        this.descn = descn;
        this.platform = platform;
        this.createTime = createTime;
        this.userId = userId;
        this.userRepoCode = userRepoCode;
        this.tenantId = tenantId;
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.appUrl = appUrl;
        this.pcUrl = pcUrl;
        this.adminUrl = adminUrl;
        this.sysEntries = sysEntries;
    }

    /** @return 主键. */
    @Id
    @Column(name = "ID", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    /**
     * @param id
     *            主键.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /** @return 外键，应用. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APP_ID")
    public OpenApp getOpenApp() {
        return this.openApp;
    }

    /**
     * @param openApp
     *            外键，应用.
     */
    public void setOpenApp(OpenApp openApp) {
        this.openApp = openApp;
    }

    /** @return 外键，系统分类. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID")
    public SysCategory getSysCategory() {
        return this.sysCategory;
    }

    /**
     * @param sysCategory
     *            外键，系统分类.
     */
    public void setSysCategory(SysCategory sysCategory) {
        this.sysCategory = sysCategory;
    }

    /** @return 图标. */
    @Column(name = "LOGO", length = 200)
    public String getLogo() {
        return this.logo;
    }

    /**
     * @param logo
     *            图标.
     */
    public void setLogo(String logo) {
        this.logo = logo;
    }

    /** @return 分类. */
    @Column(name = "TYPE", length = 50)
    public String getType() {
        return this.type;
    }

    /**
     * @param type
     *            分类.
     */
    public void setType(String type) {
        this.type = type;
    }

    /** @return 编码. */
    @Column(name = "CODE", length = 50)
    public String getCode() {
        return this.code;
    }

    /**
     * @param code
     *            编码.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /** @return 名称. */
    @Column(name = "NAME", length = 50)
    public String getName() {
        return this.name;
    }

    /**
     * @param name
     *            名称.
     */
    public void setName(String name) {
        this.name = name;
    }

    /** @return 网址. */
    @Column(name = "URL", length = 200)
    public String getUrl() {
        return this.url;
    }

    /**
     * @param url
     *            网址.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /** @return 排序. */
    @Column(name = "PRIORITY")
    public Integer getPriority() {
        return this.priority;
    }

    /**
     * @param priority
     *            排序.
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /** @return 状态. */
    @Column(name = "STATUS", length = 50)
    public String getStatus() {
        return this.status;
    }

    /**
     * @param status
     *            状态.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /** @return 备注. */
    @Column(name = "DESCN", length = 200)
    public String getDescn() {
        return this.descn;
    }

    /**
     * @param descn
     *            备注.
     */
    public void setDescn(String descn) {
        this.descn = descn;
    }

    /** @return 平台，web, app. */
    @Column(name = "PLATFORM", length = 50)
    public String getPlatform() {
        return this.platform;
    }

    /**
     * @param platform
     *            平台，web, app.
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /** @return 创建时间. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_TIME", length = 26)
    public Date getCreateTime() {
        return this.createTime;
    }

    /**
     * @param createTime
     *            创建时间.
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /** @return 创建人. */
    @Column(name = "USER_ID", length = 64)
    public String getUserId() {
        return this.userId;
    }

    /**
     * @param userId
     *            创建人.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /** @return 账号体系. */
    @Column(name = "USER_REPO_CODE", length = 64)
    public String getUserRepoCode() {
        return this.userRepoCode;
    }

    /**
     * @param userRepoCode
     *            账号体系.
     */
    public void setUserRepoCode(String userRepoCode) {
        this.userRepoCode = userRepoCode;
    }

    /** @return 租户. */
    @Column(name = "TENANT_ID", length = 64)
    public String getTenantId() {
        return this.tenantId;
    }

    /**
     * @param tenantId
     *            租户.
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /** @return app key. */
    @Column(name = "APP_KEY", length = 200)
    public String getAppKey() {
        return this.appKey;
    }

    /**
     * @param appKey
     *            app key.
     */
    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    /** @return app secret. */
    @Column(name = "APP_SECRET", length = 200)
    public String getAppSecret() {
        return this.appSecret;
    }

    /**
     * @param appSecret
     *            app secret.
     */
    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    /** @return 移动端网址. */
    @Column(name = "APP_URL", length = 200)
    public String getAppUrl() {
        return this.appUrl;
    }

    /**
     * @param appUrl
     *            移动端网址.
     */
    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    /** @return PC端网址. */
    @Column(name = "PC_URL", length = 200)
    public String getPcUrl() {
        return this.pcUrl;
    }

    /**
     * @param pcUrl
     *            PC端网址.
     */
    public void setPcUrl(String pcUrl) {
        this.pcUrl = pcUrl;
    }

    /** @return 管理端网址. */
    @Column(name = "ADMIN_URL", length = 200)
    public String getAdminUrl() {
        return this.adminUrl;
    }

    /**
     * @param adminUrl
     *            管理端网址.
     */
    public void setAdminUrl(String adminUrl) {
        this.adminUrl = adminUrl;
    }

    /** @return . */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sysInfo")
    public Set<SysEntry> getSysEntries() {
        return this.sysEntries;
    }

    /**
     * @param sysEntries
     *            .
     */
    public void setSysEntries(Set<SysEntry> sysEntries) {
        this.sysEntries = sysEntries;
    }
}
