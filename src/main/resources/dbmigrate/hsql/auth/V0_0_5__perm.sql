

-------------------------------------------------------------------------------
--  permission
-------------------------------------------------------------------------------
CREATE TABLE AUTH_PERM(
    ID BIGINT NOT NULL,
    CODE VARCHAR(200),
    NAME VARCHAR(200),
    PERM_TYPE_ID BIGINT,
    SCOPE_ID VARCHAR(50),
    CONSTRAINT PK_AUTH_PERM PRIMARY KEY(ID),
    CONSTRAINT FK_AUTH_PERM_TYPE FOREIGN KEY(PERM_TYPE_ID) REFERENCES AUTH_PERM_TYPE(ID)
);

COMMENT ON TABLE AUTH_PERM IS '权限';
COMMENT ON COLUMN AUTH_PERM.ID IS '主键';
COMMENT ON COLUMN AUTH_PERM.CODE IS '编码';
COMMENT ON COLUMN AUTH_PERM.NAME IS '名称';
COMMENT ON COLUMN AUTH_PERM.PERM_TYPE_ID IS '外键，权限类型';
COMMENT ON COLUMN AUTH_PERM.SCOPE_ID IS '租户';
