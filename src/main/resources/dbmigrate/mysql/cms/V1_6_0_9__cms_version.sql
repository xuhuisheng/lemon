

-------------------------------------------------------------------------------
--  cms version
-------------------------------------------------------------------------------
CREATE TABLE CMS_VERSION(
        ID BIGINT NOT NULL,
	CODE VARCHAR(64),
	NAME VARCHAR(50),
	USER_ID VARCHAR(64),
	CREATE_TIME DATETIME,
	PRIORITY INT,
	TENANT_ID VARCHAR(64),
	CONTENT_ID BIGINT,
        CONSTRAINT PK_CMS_VERSION PRIMARY KEY(ID),
	CONSTRAINT FK_CMS_VERSION_CONTENT FOREIGN KEY(CONTENT_ID) REFERENCES CMS_CONTENT(ID)
) ENGINE=INNODB CHARSET=UTF8;

