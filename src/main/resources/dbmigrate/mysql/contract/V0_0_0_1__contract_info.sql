

-------------------------------------------------------------------------------
--  contract info
-------------------------------------------------------------------------------
CREATE TABLE CONTRACT_INFO(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	CREATE_TIME DATETIME,
	STATUS VARCHAR(50),
	COMPANY VARCHAR(50),
	CODE VARCHAR(50),
	TYPE VARCHAR(50),
	START_TIME DATETIME,
	END_TIME DATETIME,
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_CONTRACT_INFO PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

