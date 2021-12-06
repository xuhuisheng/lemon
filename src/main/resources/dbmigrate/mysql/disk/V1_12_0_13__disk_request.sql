

-------------------------------------------------------------------------------
--  disk request
-------------------------------------------------------------------------------
CREATE TABLE DISK_REQUEST(
    ID BIGINT NOT NULL,
    MASK INT,
    DESCRIPTION VARCHAR(200),
    RESULT VARCHAR(200),

    INFO_ID BIGINT,
    USER_ID VARCHAR(64),

	CREATOR VARCHAR(64),
	CREATE_TIME DATETIME,
	UPDATER VARCHAR(64),
	UPDATE_TIME DATETIME,
	STATUS VARCHAR(50),
    TENANT_ID VARCHAR(64),
    CONSTRAINT PK_DISK_REQUEST PRIMARY KEY(ID),
    CONSTRAINT FK_DISK_REQUEST_INFO FOREIGN KEY(INFO_ID) REFERENCES DISK_INFO(ID)
) ENGINE=INNODB CHARSET=UTF8;














