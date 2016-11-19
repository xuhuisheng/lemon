

-------------------------------------------------------------------------------
--  business info
-------------------------------------------------------------------------------
CREATE TABLE BUSINESS_INFO(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	CREATE_TIME DATETIME,
	STATUS VARCHAR(50),
	CUSTOMER VARCHAR(100),
	PRODUCT VARCHAR(100),
	DESCRIPTION VARCHAR(65535),
	USER_ID VARCHAR(64),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_BUSINESS_INFO PRIMARY KEY(ID)
);

