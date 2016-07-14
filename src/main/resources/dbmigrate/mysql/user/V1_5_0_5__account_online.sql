

-------------------------------------------------------------------------------
--  account online
-------------------------------------------------------------------------------
CREATE TABLE ACCOUNT_ONLINE(
        ID BIGINT NOT NULL,
	ACCOUNT VARCHAR(200),
	SESSION_ID VARCHAR(200),
	LOGIN_TIME DATETIME,
        CONSTRAINT PK_ACCOUNT_ONLINE PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

