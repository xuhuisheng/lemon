

-------------------------------------------------------------------------------
--  pim task
-------------------------------------------------------------------------------
CREATE TABLE PIM_PLAN(
        ID BIGINT NOT NULL,
        
	NAME VARCHAR(200),
	CONTENT VARCHAR(200),
	START_TIME DATETIME,
	END_TIME DATETIME,
	CATALOG VARCHAR(50),
	PRIORITY INTEGER,
	ASSIGNEE VARCHAR(64),

	STATUS VARCHAR(50),

	CREATE_TIME DATETIME,
	USER_ID VARCHAR(64),
	TENANT_ID VARCHAR(50),
        CONSTRAINT PK_PIM_PLAN PRIMARY KEY(ID)
);


