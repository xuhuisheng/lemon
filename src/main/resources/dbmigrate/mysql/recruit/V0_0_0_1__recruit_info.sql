

-------------------------------------------------------------------------------
--  recruit info
-------------------------------------------------------------------------------
CREATE TABLE RECRUIT_INFO(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	CARD_TYPE VARCHAR(50),
	CARD_VALUE VARCHAR(50),
	MOBILE VARCHAR(50),
	EMAIL VARCHAR(100),
	CREATE_TIME DATETIME,
	STATUS VARCHAR(50),
	TYPE VARCHAR(50),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_RECRUIT_INFO PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

