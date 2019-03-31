

-------------------------------------------------------------------------------
--  comment thread
-------------------------------------------------------------------------------
CREATE TABLE COMMENT_THREAD(
    ID BIGINT NOT NULL,
	URL VARCHAR(200),
	SOURCE_ID VARCHAR(50),
    CREATE_TIME TIMESTAMP,
	TENANT_ID VARCHAR(64),
    CONSTRAINT PK_COMMENT_THREAD PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

