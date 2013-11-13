package com.mossle.security.region;

public class RegionDTO {
    private Long id;
    private String name;
    private String key;
    private Long parentId;
    private String entityType;
    private Long entityId;
    private String entityName;
    private String regionPath;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
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

    public String toString() {
        return "RegionDTO{id:" + id + ",name:" + name + ",key:" + key
                + ",parentId:" + parentId + ",entityType:" + entityType
                + ",entityId:" + entityId + ",entityName:" + entityName
                + ",regionPath:" + regionPath + "}";
    }
}
