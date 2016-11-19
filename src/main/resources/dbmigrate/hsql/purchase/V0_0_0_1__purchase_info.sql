

-------------------------------------------------------------------------------
--  purchase info
-------------------------------------------------------------------------------
CREATE TABLE PURCHASE_INFO(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	CREATE_TIME DATETIME,
	STATUS VARCHAR(50),
	SUPPLIER VARCHAR(100),
	GOODS VARCHAR(100),
	DESCRIPTION VARCHAR(65535),
	USER_ID VARCHAR(64),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_PURCHASE_INFO PRIMARY KEY(ID)
);

