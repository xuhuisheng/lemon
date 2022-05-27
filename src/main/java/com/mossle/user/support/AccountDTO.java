package com.mossle.user.support;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AccountDTO {
    private String username;
    private String code;

    @NotNull
    @Size(max = 50)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @NotNull
    @Size(max = 50)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
