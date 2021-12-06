

-------------------------------------------------------------------------------
--  disk recent
-------------------------------------------------------------------------------
CREATE TABLE DISK_RECENT(
    ID BIGINT NOT NULL,
    NAME VARCHAR(200),
    TYPE VARCHAR(50),
    INFO_ID BIGINT,

	CREATOR VARCHAR(64),
	CREATE_TIME DATETIME,
	UPDATER VARCHAR(64),
	UPDATE_TIME DATETIME,
	STATUS VARCHAR(50),
    TENANT_ID VARCHAR(64),
    CONSTRAINT PK_DISK_RECENT PRIMARY KEY(ID),
    CONSTRAINT FK_DISK_RECENT_INFO FOREIGN KEY(INFO_ID) REFERENCES DISK_INFO(ID)
) ENGINE=INNODB CHARSET=UTF8;












