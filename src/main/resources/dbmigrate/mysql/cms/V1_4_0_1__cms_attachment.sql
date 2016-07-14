

-------------------------------------------------------------------------------
--  cms attachment
-------------------------------------------------------------------------------
CREATE TABLE CMS_ATTACHMENT(
        ID BIGINT NOT NULL,
	TYPE VARCHAR(200),
	NAME VARCHAR(200),
	PATH VARCHAR(200),
	SIZE INT,
	HEIGHT INT,
	WIDTH INT,
	CREATE_TIME TIMESTAMP,
	USER_ID VARCHAR(200),
	ARTICLE_ID BIGINT,
        CONSTRAINT PK_CMS_ATTACHMENT PRIMARY KEY(ID),
	CONSTRAINT FK_CMS_ATTACHMENT_ARTICLE FOREIGN KEY(ARTICLE_ID) REFERENCES CMS_ARTICLE(ID)
) ENGINE=INNODB CHARSET=UTF8;
