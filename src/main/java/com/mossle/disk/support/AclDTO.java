package com.mossle.disk.support;

public class AclDTO {
    private long infoCode;
    private String memberCode;
    private String memberType;
    private String memberName;
    private String mask;

    public long getInfoCode() {
        return infoCode;
    }

    public void setInfoCode(long infoCode) {
        this.infoCode = infoCode;
    }

    public String getMemberCode() {
        return memberCode;
    }

    public void setMemberCode(String memberCode) {
        this.memberCode = memberCode;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }
}
