

-------------------------------------------------------------------------------
--  oss data
-------------------------------------------------------------------------------
CREATE TABLE OSS_DATA(
    ID BIGINT NOT NULL,
    CODE VARCHAR(200),
    DATA BLOB,
    STATUS VARCHAR(50),
    CREATE_TIME TIMESTAMP,
    DESCRIPTION VARCHAR(200),
    USER_ID VARCHAR(64),
    TENANT_ID VARCHAR(50),
    CONSTRAINT PK_OSS_DATA PRIMARY KEY(ID)
);

COMMENT ON TABLE OSS_DATA              IS '数据库保存文件';
COMMENT ON COLUMN OSS_DATA.ID          IS '主键';
COMMENT ON COLUMN OSS_DATA.CODE        IS '编码';
COMMENT ON COLUMN OSS_DATA.DATA        IS '数据';
COMMENT ON COLUMN OSS_DATA.STATUS      IS '状态: active,inactive';
COMMENT ON COLUMN OSS_DATA.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN OSS_DATA.DESCRIPTION IS '描述(备用)';
COMMENT ON COLUMN OSS_DATA.USER_ID     IS '创建人(备用)';
COMMENT ON COLUMN OSS_DATA.TENANT_ID   IS '租户';
