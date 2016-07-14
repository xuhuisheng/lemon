

-------------------------------------------------------------------------------
--  sendsms history
-------------------------------------------------------------------------------
CREATE TABLE SENDSMS_HISTORY(
        ID BIGINT,
	MOBILE VARCHAR(50),
        MESSAGE VARCHAR(200),
	CREATE_TIME TIMESTAMP,
	STATUS VARCHAR(50),
	INFO VARCHAR(200),
	CONFIG_ID BIGINT,
	TENANT_ID VARCHAR(50),
        CONSTRAINT PK_SENDSMS_HISTORY PRIMARY KEY(ID),
        CONSTRAINT FK_SENDSMS_HISTORY_CONFIG FOREIGN KEY(CONFIG_ID) REFERENCES SENDSMS_CONFIG(ID)
);

