

-------------------------------------------------------------------------------
--  feedback info
-------------------------------------------------------------------------------
CREATE TABLE FEEDBACK_INFO(
        ID BIGINT NOT NULL,
	CONTENT VARCHAR(200),
	CONTACT VARCHAR(200),
	STATUS VARCHAR(50),
	CREATE_TIME DATETIME,
	USER_ID VARCHAR(64),
	CATALOG_ID BIGINT,
	CONSTRAINT PK_FEEDBACK_INFO PRIMARY KEY(ID),
	CONSTRAINT FK_FEEDBACK_INFO_CATALOG FOREIGN KEY(CATALOG_ID) REFERENCES FEEDBACK_CATALOG(ID)
) ENGINE=INNODB CHARSET=UTF8;

