

-------------------------------------------------------------------------------
--  role
-------------------------------------------------------------------------------
CREATE TABLE AUTH_ROLE(
        ID BIGINT NOT NULL,
        NAME VARCHAR(50),
        DESCN VARCHAR(200),
	ROLE_DEF_ID BIGINT,
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_AUTH_ROLE PRIMARY KEY(ID),
        CONSTRAINT FK_AUTH_ROLE_DEF FOREIGN KEY(ROLE_DEF_ID) REFERENCES AUTH_ROLE_DEF(ID)
);

COMMENT ON TABLE AUTH_ROLE IS '角色';
COMMENT ON COLUMN AUTH_ROLE.ID IS '主键';
COMMENT ON COLUMN AUTH_ROLE.NAME IS '名称';
COMMENT ON COLUMN AUTH_ROLE.DESCN IS '备注';
COMMENT ON COLUMN AUTH_ROLE.ROLE_DEF_ID IS '外键，角色定义';
COMMENT ON COLUMN AUTH_ROLE.SCOPE_ID IS '租户';
