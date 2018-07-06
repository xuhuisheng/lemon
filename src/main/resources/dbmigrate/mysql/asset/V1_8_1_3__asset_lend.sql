

-------------------------------------------------------------------------------
--  asset lend
-------------------------------------------------------------------------------
CREATE TABLE ASSET_LEND(
        ID BIGINT NOT NULL,
	USER_ID VARCHAR(64),
	LEND_DATE DATETIME,
	RETURN_DATE DATETIME,
	STATUS VARCHAR(50),
	OPERATOR VARCHAR(64),
	DESCRIPTION VARCHAR(200),
	INFO_ID BIGINT,
	CONSTRAINT PK_ASSET_LEND PRIMARY KEY(ID),
        CONSTRAINT FK_ASSET_LEND_INFO FOREIGN KEY(INFO_ID) REFERENCES ASSET_INFO(ID)
) ENGINE=INNODB CHARSET=UTF8;

