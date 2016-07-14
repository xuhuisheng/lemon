

-------------------------------------------------------------------------------
--  audit base
-------------------------------------------------------------------------------
CREATE TABLE AUDIT_BASE(
        ID BIGINT NOT NULL,
	USER VARCHAR(200),
	RESOURCE_TYPE VARCHAR(200),
	RESOURCE_ID VARCHAR(200),
	ACTION VARCHAR(200),
	RESULT VARCHAR(200),
	APPLICATION VARCHAR(200),
	AUDIT_TIME TIMESTAMP,
        CLIENT VARCHAR(200),
	SERVER VARCHAR(200),
	DESCRIPTION VARCHAR(200),
        CONSTRAINT PK_AUDIT_BASE PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

