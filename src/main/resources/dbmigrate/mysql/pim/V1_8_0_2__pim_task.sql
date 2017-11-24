

-------------------------------------------------------------------------------
--  pim task
-------------------------------------------------------------------------------
CREATE TABLE PIM_TASK(
        ID BIGINT NOT NULL,
        
	NAME VARCHAR(200),
	CONTENT VARCHAR(200),
	START_TIME DATETIME,
	END_TIME DATETIME,
	CATALOG VARCHAR(50),
	PRIORITY INTEGER,
	TAG VARCHAR(200),
	ASSIGNEE VARCHAR(64),
	CANDIDATE VARCHAR(200),
	STYLE VARCHAR(50),

	STATUS VARCHAR(50),

	CREATE_TIME DATETIME,
	USER_ID VARCHAR(64),
	TENANT_ID VARCHAR(50),
        CONSTRAINT PK_PIM_TASK PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;


