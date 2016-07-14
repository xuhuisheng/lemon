package com.mossle.api.user;

public class UserDTO {
    /** 用户在数据库里的唯一标识. */
    private String id;

    /** 用户登录使用的账号. */
    private String username;

    /** 外部主键. */
    private String ref;

    /** 账号体系. */
    private String userRepoRef;

    /** 用户的状态. */
    private int status;

    /** 显示名. */
    private String displayName;

    /** 昵称. */
    private String nickName;

    /** 邮箱. */
    private String email;

    /** 手机. */
    private String mobile;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getUserRepoRef() {
        return userRepoRef;
    }

    public void setUserRepoRef(String userRepoRef) {
        this.userRepoRef = userRepoRef;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
