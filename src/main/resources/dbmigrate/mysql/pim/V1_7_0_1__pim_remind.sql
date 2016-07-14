

-------------------------------------------------------------------------------
--  pim remind
-------------------------------------------------------------------------------
CREATE TABLE PIM_REMIND(
        ID BIGINT NOT NULL,
	TYPE VARCHAR(50),
	REPEAT_TYPE VARCHAR(64),
	REPEAT_PEROID INT,
	PRIORITY INT,
	REMIND_TIME VARCHAR(50),
	DESCRIPTION VARCHAR(200),
	CREATE_TIME DATETIME,
	STATUS VARCHAR(50),
	USER_ID VARCHAR(64),
	REF VARCHAR(200),
        CONSTRAINT PK_PIM_REMIND PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;














