

-------------------------------------------------------------------------------
--  role def
-------------------------------------------------------------------------------
CREATE TABLE AUTH_ROLE_DEF(
        ID BIGINT NOT NULL,
        NAME VARCHAR(50),
        DESCN VARCHAR(200),
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_AUTH_ROLE_DEF PRIMARY KEY(ID)
);

COMMENT ON TABLE AUTH_ROLE_DEF IS '角色定义';
COMMENT ON COLUMN AUTH_ROLE_DEF.ID IS '主键';
COMMENT ON COLUMN AUTH_ROLE_DEF.NAME IS '名称';
COMMENT ON COLUMN AUTH_ROLE_DEF.DESCN IS '备注';
COMMENT ON COLUMN AUTH_ROLE_DEF.SCOPE_ID IS '租户';
