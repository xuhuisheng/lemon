

-------------------------------------------------------------------------------
--  expense info
-------------------------------------------------------------------------------
CREATE TABLE EXPENSE_INFO(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	CREATE_TIME DATETIME,
	STATUS VARCHAR(50),
	TYPE VARCHAR(50),
	MONEY DOUBLE,
	START_TIME DATETIME,
	END_TIME DATETIME,
	HEAD_COUNT INTEGER,
	PERSON VARCHAR(200),
	TRAFFIC VARCHAR(100),
	COUNTRY VARCHAR(100),
	ADDRESS VARCHAR(100),
	THING VARCHAR(100),
	USER_ID VARCHAR(64),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_EXPENSE_INFO PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

