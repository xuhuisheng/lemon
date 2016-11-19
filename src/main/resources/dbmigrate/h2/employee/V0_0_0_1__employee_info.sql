

-------------------------------------------------------------------------------
--  employee info
-------------------------------------------------------------------------------
CREATE TABLE EMPLOYEE_INFO(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	CREATE_TIME DATETIME,
	STATUS VARCHAR(50),
	DEPARTMENT VARCHAR(50),
	COMPANY VARCHAR(50),
	POSITION VARCHAR(50),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_EMPLOYEE_INFO PRIMARY KEY(ID)
);

