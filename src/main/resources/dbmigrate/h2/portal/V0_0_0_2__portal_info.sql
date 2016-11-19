

-------------------------------------------------------------------------------
--  portal info
-------------------------------------------------------------------------------
CREATE TABLE PORTAL_INFO(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	DESCRIPTION VARCHAR(200),
        USER_ID VARCHAR(64),
	CREATE_TIME DATETIME,
	COLUMN_LAYOUT VARCHAR(100),
	SHARED_STATUS VARCHAR(10),
	GLOBAL_STATUS VARCHAR(10),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_AUDIT_INFO PRIMARY KEY(ID)
);

