

-------------------------------------------------------------------------------
--  sequence info
-------------------------------------------------------------------------------
CREATE TABLE SEQUENCE_INFO(
        ID BIGINT NOT NULL,
	NAME VARCHAR(100),
	CODE VARCHAR(50),
	CONTENT VARCHAR(100),
	VALUE INT,
	UPDATE_DATE DATETIME,
	CREATE_TIME DATETIME,
	USER_ID VARCHAR(64),
	TENANT_ID VARCHAR(50),
        CONSTRAINT PK_SEQUENCE_INFO PRIMARY KEY(ID)
);

