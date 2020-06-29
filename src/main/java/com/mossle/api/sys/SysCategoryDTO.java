package com.mossle.api.sys;

import java.util.ArrayList;
import java.util.List;

public class SysCategoryDTO {
    private String code;
    private String name;
    private List<SysInfoDTO> children = new ArrayList<SysInfoDTO>();

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

    public List<SysInfoDTO> getChildren() {
        return children;
    }

    public void setChildren(List<SysInfoDTO> children) {
        this.children = children;
    }
}
