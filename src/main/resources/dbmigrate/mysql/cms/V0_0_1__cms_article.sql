

-------------------------------------------------------------------------------
--  cms article
-------------------------------------------------------------------------------
CREATE TABLE CMS_ARTICLE(
        ID BIGINT auto_increment,
	NAME VARCHAR(200),
	CONTENT VARCHAR(200),
	TYPE INTEGER,
	CREATE_TIME TIMESTAMP,
	USER_ID BIGINT,
	STATUS INTEGER,
        CONSTRAINT PK_CMS_ARTICLE PRIMARY KEY(ID)
) engine=innodb;

