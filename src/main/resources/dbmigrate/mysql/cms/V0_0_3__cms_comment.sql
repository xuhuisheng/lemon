

-------------------------------------------------------------------------------
--  cms comment
-------------------------------------------------------------------------------
CREATE TABLE CMS_COMMENT(
        ID BIGINT AUTO_INCREMENT,
	TITLE VARCHAR(200),
	CONTENT VARCHAR(200),
	STATUS INT,
	CREATE_TIME TIMESTAMP,
	USER_ID VARCHAR(200),
	ARTICLE_ID BIGINT,
        CONSTRAINT PK_CMS_COMMENT PRIMARY KEY(ID),
	CONSTRAINT FK_CMS_COMMENT_ARTICLE FOREIGN KEY(ARTICLE_ID) REFERENCES CMS_ARTICLE(ID)
) ENGINE=INNODB CHARSET=UTF8;
