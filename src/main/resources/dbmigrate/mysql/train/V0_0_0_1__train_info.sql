

-------------------------------------------------------------------------------
--  train info
-------------------------------------------------------------------------------
CREATE TABLE TRAIN_INFO(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	CREATE_TIME DATETIME,
	STATUS VARCHAR(50),
	TYPE VARCHAR(50),
	START_TIME DATETIME,
	END_TIME DATETIME,
	CONTENT TEXT,
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_TRAIN_INFO PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

