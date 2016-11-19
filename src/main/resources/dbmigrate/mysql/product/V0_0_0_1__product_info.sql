

-------------------------------------------------------------------------------
--  product info
-------------------------------------------------------------------------------
CREATE TABLE PRODUCT_INFO(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	CREATE_TIME DATETIME,
	STATUS VARCHAR(50),
	TYPE VARCHAR(50),
	DESCRIPTION TEXT,
	USER_ID VARCHAR(64),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_PRODUCT_INFO PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

