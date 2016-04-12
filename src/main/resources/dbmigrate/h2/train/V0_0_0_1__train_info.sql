

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
	CONTENT VARCHAR(65535),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_TRAIN_INFO PRIMARY KEY(ID)
);

