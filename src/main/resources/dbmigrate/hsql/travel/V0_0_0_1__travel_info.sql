

-------------------------------------------------------------------------------
--  travel info
-------------------------------------------------------------------------------
CREATE TABLE TRAVEL_INFO(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	CREATE_TIME DATETIME,
	STATUS VARCHAR(50),
	TYPE VARCHAR(50),
	LOCATION VARCHAR(100),
	START_TIME DATETIME,
	END_TIME DATETIME,
	USER_ID VARCHAR(64),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_TRAVEL_INFO PRIMARY KEY(ID)
);

