

-------------------------------------------------------------------------------
--  account lock log
-------------------------------------------------------------------------------
CREATE TABLE ACCOUNT_LOCK_LOG(
        ID BIGINT NOT NULL,
	TYPE VARCHAR(200),
	USERNAME VARCHAR(64),
	LOCK_TIME DATETIME,
        CONSTRAINT PK_ACCOUNT_LOCK_LOG PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

