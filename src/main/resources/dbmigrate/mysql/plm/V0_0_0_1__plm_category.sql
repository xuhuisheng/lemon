

-------------------------------------------------------------------------------
--  plm category
-------------------------------------------------------------------------------
CREATE TABLE PLM_CATEGORY(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	PRIORITY INT,
	STATUS VARCHAR(50),
	CREATE_TIME DATETIME,
	USER_ID VARCHAR(64),
	CONSTRAINT PK_PLM_CATEGORY PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

