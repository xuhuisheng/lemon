

-------------------------------------------------------------------------------
--  pim note
-------------------------------------------------------------------------------
CREATE TABLE PIM_NOTE(
        ID BIGINT NOT NULL,
	TITLE VARCHAR(100),
        CONTENT VARCHAR(200),
	CREATE_TIME DATETIME,
	USER_ID VARCHAR(64),
        CONSTRAINT PK_PIM_NOTE PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

