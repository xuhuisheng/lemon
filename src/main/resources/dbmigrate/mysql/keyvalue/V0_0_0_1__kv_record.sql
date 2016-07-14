

-------------------------------------------------------------------------------
--  kv record
-------------------------------------------------------------------------------
CREATE TABLE KV_RECORD(
        ID BIGINT NOT NULL,
	CATEGORY VARCHAR(200),
	STATUS INT,
	REF VARCHAR(200),
	USER_ID VARCHAR(64),
	CREATE_TIME DATETIME,
	NAME VARCHAR(100),
	FORM_TEMPLATE_CODE VARCHAR(200),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_KV_RECORD PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;












