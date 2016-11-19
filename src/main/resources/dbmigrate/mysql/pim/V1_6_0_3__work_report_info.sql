

-------------------------------------------------------------------------------
--  work report info
-------------------------------------------------------------------------------
CREATE TABLE WORK_REPORT_INFO(
        ID BIGINT NOT NULL,
	TYPE VARCHAR(50),
	CONTENT VARCHAR(200),
	REPORT_DATE DATE,
	CREATE_TIME DATETIME,
	USER_ID VARCHAR(64),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_WORK_REPORT_INFO PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

