

-------------------------------------------------------------------------------
--  disk share
-------------------------------------------------------------------------------
CREATE TABLE DISK_SHARE(
        ID BIGINT NOT NULL,
	SHARE_TYPE VARCHAR(50),
	SHARE_TIME DATETIME,
	INFO_ID BIGINT,

	NAME VARCHAR(200),
	CREATOR VARCHAR(64),
	TYPE VARCHAR(64),
	DIR_TYPE INT,

	COUNT_VIEW INT,
	COUNT_SAVE INT,
	COUNT_DOWNLOAD INT,

        CONSTRAINT PK_DISK_SHARE PRIMARY KEY(ID),
	CONSTRAINT FK_DISK_SHARE_INFO FOREIGN KEY (INFO_ID) REFERENCES DISK_INFO(ID)
);



