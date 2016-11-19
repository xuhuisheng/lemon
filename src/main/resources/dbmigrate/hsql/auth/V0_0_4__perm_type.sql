

-------------------------------------------------------------------------------
--  permission type
-------------------------------------------------------------------------------
CREATE TABLE AUTH_PERM_TYPE(
        ID BIGINT NOT NULL,
        NAME VARCHAR(50),
	TYPE INTEGER,
	PRIORITY INTEGER,
        DESCN VARCHAR(200),
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_AUTH_PERM_TYPE PRIMARY KEY(ID)
);

COMMENT ON TABLE AUTH_PERM_TYPE IS '权限类型';
COMMENT ON COLUMN AUTH_PERM_TYPE.ID IS '主键';
COMMENT ON COLUMN AUTH_PERM_TYPE.NAME IS '名称';
COMMENT ON COLUMN AUTH_PERM_TYPE.TYPE IS '类型';
COMMENT ON COLUMN AUTH_PERM_TYPE.PRIORITY IS '排序';
COMMENT ON COLUMN AUTH_PERM_TYPE.DESCN IS '备注';
COMMENT ON COLUMN AUTH_PERM_TYPE.SCOPE_ID IS '租户';
