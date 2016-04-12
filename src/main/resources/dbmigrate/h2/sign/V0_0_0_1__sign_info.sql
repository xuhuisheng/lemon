

-------------------------------------------------------------------------------
--  sign info
-------------------------------------------------------------------------------
CREATE TABLE SIGN_INFO(
        ID BIGINT NOT NULL,
	USER_ID VARCHAR(64),
	CREATE_TIME DATETIME,
	CATALOG VARCHAR(50),
	TYPE VARCHAR(50),
	REF VARCHAR(100),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_SIGN_INFO PRIMARY KEY(ID)
);

