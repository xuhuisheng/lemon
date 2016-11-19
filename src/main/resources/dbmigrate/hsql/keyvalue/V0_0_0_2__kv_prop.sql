

-------------------------------------------------------------------------------
--  kv property
-------------------------------------------------------------------------------
CREATE TABLE KV_PROP(
        ID BIGINT NOT NULL,
	CODE VARCHAR(200),
	TYPE INT,
	VALUE VARCHAR(200),
	TENANT_ID VARCHAR(64),
	RECORD_ID BIGINT,
        CONSTRAINT PK_KV_PROP PRIMARY KEY(ID),
        CONSTRAINT FK_KV_PROP_RECORD FOREIGN KEY(RECORD_ID) REFERENCES KV_RECORD(ID)
);

COMMENT ON TABLE KV_PROP IS 'keyvalue属性';
COMMENT ON COLUMN KV_PROP.ID IS '主键';
COMMENT ON COLUMN KV_PROP.CODE IS '编码';
COMMENT ON COLUMN KV_PROP.TYPE IS '分类';
COMMENT ON COLUMN KV_PROP.VALUE IS '值';
COMMENT ON COLUMN KV_PROP.TENANT_ID IS '租户';
COMMENT ON COLUMN KV_PROP.RECORD_ID IS '外键，记录';

