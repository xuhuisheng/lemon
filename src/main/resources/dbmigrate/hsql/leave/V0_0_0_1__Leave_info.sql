

-------------------------------------------------------------------------------
--  leave info
-------------------------------------------------------------------------------
CREATE TABLE LEAVE_INFO(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	CREATE_TIME DATETIME,
	STATUS VARCHAR(50),
	TYPE VARCHAR(50),
	START_TIME DATETIME,
	END_TIME DATETIME,
	EMPLOYEE_ID VARCHAR(64),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_LEAVE_INFO PRIMARY KEY(ID)
);

