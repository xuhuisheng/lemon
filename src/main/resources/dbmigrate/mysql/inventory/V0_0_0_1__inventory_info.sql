

-------------------------------------------------------------------------------
--  inventory info
-------------------------------------------------------------------------------
CREATE TABLE INVENTORY_INFO(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	CREATE_TIME DATETIME,
	STATUS VARCHAR(50),
	GOODS VARCHAR(100),
	SUPPLIER VARCHAR(100),
	DESCRIPTION TEXT,
	USER_ID VARCHAR(64),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_INVENTORY_INFO PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

