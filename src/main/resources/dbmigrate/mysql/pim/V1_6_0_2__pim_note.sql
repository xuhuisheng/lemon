

-------------------------------------------------------------------------------
--  pim note
-------------------------------------------------------------------------------
CREATE TABLE PIM_NOTE(
        ID BIGINT AUTO_INCREMENT,
	TITLE VARCHAR(100),
        CONTENT VARCHAR(200),
	CREATE_TIME DATETIME,
	USER_ID VARCHAR(64),
        CONSTRAINT PK_PIM_NOTE PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

