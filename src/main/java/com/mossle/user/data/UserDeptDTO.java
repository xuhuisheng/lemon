package com.mossle.user.data;

import java.util.ArrayList;
import java.util.List;

public class UserDeptDTO {
    private String code;
    private String name;
    private String type;
    private String leader;
    private List<UserDeptDTO> children = new ArrayList<UserDeptDTO>();
    private String parentCode;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public List<UserDeptDTO> getChildren() {
        return children;
    }

    public void setChildren(List<UserDeptDTO> children) {
        this.children = children;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }
}
