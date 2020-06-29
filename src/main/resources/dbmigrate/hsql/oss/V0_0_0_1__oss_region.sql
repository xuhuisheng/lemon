

-------------------------------------------------------------------------------
--  oss region
-------------------------------------------------------------------------------
CREATE TABLE OSS_REGION(
    ID BIGINT NOT NULL,
    CODE VARCHAR(50),
    NAME VARCHAR(200),
    STATUS VARCHAR(50),
    CREATE_TIME TIMESTAMP,
    DESCRIPTION VARCHAR(200),
    TENANT_ID VARCHAR(50),
    CONSTRAINT PK_OSS_REGION PRIMARY KEY(ID)
);

COMMENT ON TABLE OSS_REGION              IS '地域';
COMMENT ON COLUMN OSS_REGION.ID          IS '主键';
COMMENT ON COLUMN OSS_REGION.CODE        IS '编码（备用）';
COMMENT ON COLUMN OSS_REGION.NAME        IS '名称';
COMMENT ON COLUMN OSS_REGION.STATUS      IS '状态: active,inactive';
COMMENT ON COLUMN OSS_REGION.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN OSS_REGION.DESCRIPTION IS '描述(备用)';
COMMENT ON COLUMN OSS_REGION.TENANT_ID   IS '租户';
