

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
) ENGINE=INNODB CHARSET=UTF8;









