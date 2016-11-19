

-------------------------------------------------------------------------------
--  performance info
-------------------------------------------------------------------------------
CREATE TABLE PERFORMANCE_INFO(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	CREATE_TIME DATETIME,
	STATUS VARCHAR(50),
	TYPE VARCHAR(50),
	START_TIME DATETIME,
	END_TIME DATETIME,
	EMPLOYEE_ID VARCHAR(64),
	SUPERIOUR_ID VARCHAR(64),
	SCORE VARCHAR(50),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_PERFORMANCE_INFO PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

