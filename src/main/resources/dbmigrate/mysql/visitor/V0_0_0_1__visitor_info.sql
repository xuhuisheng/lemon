

-------------------------------------------------------------------------------
--  visitor info
-------------------------------------------------------------------------------
CREATE TABLE VISITOR_INFO(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	MOBILE VARCHAR(50),
	COMPANY_NAME VARCHAR(200),
	DESCRIPTION VARCHAR(200),
	CREATE_TIME DATETIME,
	VISIT_TIME DATETIME,
	LEAVE_TIME DATETIME,
	ENTER_TIME DATETIME,
	STATUS INTEGER,
	USER_ID VARCHAR(64),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_VISITOR_INFO PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

