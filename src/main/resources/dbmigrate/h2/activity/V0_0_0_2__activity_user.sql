

-------------------------------------------------------------------------------
--  activity user
-------------------------------------------------------------------------------
CREATE TABLE ACTIVITY_USER(
        ID BIGINT NOT NULL,
	PRIORITY INTEGER,
	CREATE_TIME DATETIME,
	USER_ID VARCHAR(64),
	TENANT_ID VARCHAR(64),
	INFO_ID BIGINT,
        CONSTRAINT PK_ACTIVITY_USER PRIMARY KEY(ID),
        CONSTRAINT FK_ACTIVITY_USER_INFO FOREIGN KEY(INFO_ID) REFERENCES ACTIVITY_INFO(ID)
);

