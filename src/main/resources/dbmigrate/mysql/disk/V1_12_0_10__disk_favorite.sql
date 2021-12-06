

-------------------------------------------------------------------------------
--  disk favorite
-------------------------------------------------------------------------------
CREATE TABLE DISK_FAVORITE(
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
    CONSTRAINT PK_DISK_FAVORITE PRIMARY KEY(ID),
    CONSTRAINT FK_DISK_FAVORITE_INFO FOREIGN KEY(INFO_ID) REFERENCES DISK_INFO(ID)
) ENGINE=INNODB CHARSET=UTF8;












