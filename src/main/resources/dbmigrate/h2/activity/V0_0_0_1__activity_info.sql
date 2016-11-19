

-------------------------------------------------------------------------------
--  activity info
-------------------------------------------------------------------------------
CREATE TABLE ACTIVITY_INFO(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	CONTENT VARCHAR(65535),
	LOCATION VARCHAR(200),
	STATUS VARCHAR(50),
	CREATE_TIME DATETIME,
	START_TIME DATETIME,
	END_TIME DATETIME,
	OPEN_TIME DATETIME,
	CLOSE_TIME DATETIME,
	HEAD_COUNT INTEGER,
	USER_ID VARCHAR(64),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_ACTIVITY_INFO PRIMARY KEY(ID)
);

