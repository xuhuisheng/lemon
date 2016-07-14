package com.mossle.api.tenant;

public class TenantDTO {
    /** 普通的管理后台的类型. */
    public static final String TYPE_DEFAULT = "default";

    /** cms前端显示静态页面的类型. */
    public static final String TYPE_CMS = "cms";

    /** 数据库里的逻辑id. */
    private String id;

    /** 用户可以理解的名称 */
    private String name;

    /** 可以放到url里的代码. */
    private String code;

    /** 与外部系统协商的唯一标识. */
    private String ref;

    /** 这个应用的资源是否会被其他应用共享. */
    private boolean shared;

    /** 我们可以认为每个scope都是一个租户，每个租户当然需要关联一种登录的方式. */
    private String userRepoRef;

    /** 类型. */
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public String getUserRepoRef() {
        return userRepoRef;
    }

    public void setUserRepoRef(String userRepoRef) {
        this.userRepoRef = userRepoRef;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
