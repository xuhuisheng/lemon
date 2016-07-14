

-------------------------------------------------------------------------------
--  customer info
-------------------------------------------------------------------------------
CREATE TABLE CUSTOMER_INFO(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	CREATE_TIME DATETIME,
	STATUS VARCHAR(50),
	TYPE VARCHAR(50),
	ADDRESS VARCHAR(200),
	CONTACT VARCHAR(100),
	COMPANY VARCHAR(100),
	DESCRIPTION VARCHAR(65535),
	USER_ID VARCHAR(64),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_CUSTOMER_INFO PRIMARY KEY(ID)
);

