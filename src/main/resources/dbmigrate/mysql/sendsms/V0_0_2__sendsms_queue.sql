

-------------------------------------------------------------------------------
--  sendsms queue
-------------------------------------------------------------------------------
CREATE TABLE SENDSMS_QUEUE(
        ID BIGINT,
	MOBILE VARCHAR(50),
        MESSAGE VARCHAR(200),
	CREATE_TIME TIMESTAMP,
	STATUS VARCHAR(50),
	INFO VARCHAR(200),
	CONFIG_ID BIGINT,
	TENANT_ID VARCHAR(50),
        CONSTRAINT PK_SENDSMS_QUEUE PRIMARY KEY(ID),
        CONSTRAINT FK_SENDSMS_QUEUE_CONFIG FOREIGN KEY(CONFIG_ID) REFERENCES SENDSMS_CONFIG(ID)
) ENGINE=INNODB CHARSET=UTF8;

