package com.mossle.disk.support;

import java.util.ArrayList;
import java.util.List;

public class BatchAclDTO {
    private long code;
    private List<AclDTO> list = new ArrayList<AclDTO>();

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public List<AclDTO> getList() {
        return list;
    }

    public void setList(List<AclDTO> list) {
        this.list = list;
    }
}
