package com.mossle.security.region;

public class RegionRoleDTO {
    private Long id;
    private Long regionId;
    private String regionName;
    private String entityType;
    private Long entityId;
    private String entityName;
    private String regionPath;
    private Long roleId;
    private String roleName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getRegionPath() {
        return regionPath;
    }

    public void setRegionPath(String regionPath) {
        this.regionPath = regionPath;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return roleName + "(" + entityName + ")";
    }

    public String toString() {
        return "RegionRoleDTO{id:" + id + ",regionId:" + regionId
                + ",regionName:" + regionName + ",entityType:" + entityType
                + ",entityId:" + entityId + ",entityName:" + entityName
                + ",regionPath:" + regionPath + ",roleId:" + roleId
                + ",roleName:" + roleName + "}";
    }
}
