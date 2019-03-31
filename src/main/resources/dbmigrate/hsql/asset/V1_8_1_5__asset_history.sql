

-------------------------------------------------------------------------------
--  asset history
-------------------------------------------------------------------------------
CREATE TABLE ASSET_HISTORY(
        ID BIGINT NOT NULL,
	BEFORE_USER_ID VARCHAR(64),
	AFTER_USER_ID VARCHAR(64),
	UPDATE_TIME DATETIME,
	REASON VARCHAR(200),
	STATUS VARCHAR(50),
	OPERATOR VARCHAR(64),
	DESCRIPTION VARCHAR(200),
	INFO_ID BIGINT,
	CONSTRAINT PK_ASSET_HISTORY PRIMARY KEY(ID),
        CONSTRAINT FK_ASSET_HISTORY_INFO FOREIGN KEY(INFO_ID) REFERENCES ASSET_INFO(ID)
);

