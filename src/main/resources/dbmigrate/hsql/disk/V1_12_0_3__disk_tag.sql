

-------------------------------------------------------------------------------
--  disk tag
-------------------------------------------------------------------------------
CREATE TABLE DISK_TAG(
    ID BIGINT NOT NULL,
    CODE VARCHAR(50),
    NAME VARCHAR(50),
    TENANT_ID VARCHAR(64),
    CONSTRAINT PK_DISK_TAG PRIMARY KEY(ID)
);

CREATE INDEX IDX_DISK_TAG_CODE ON DISK_TAG(CODE);

COMMENT ON TABLE DISK_TAG IS '标签';
COMMENT ON COLUMN DISK_TAG.ID IS 'id';
COMMENT ON COLUMN DISK_TAG.CODE IS '编码';
COMMENT ON COLUMN DISK_TAG.NAME IS '名称';
COMMENT ON COLUMN DISK_TAG.TENANT_ID IS '租户';

