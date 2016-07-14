

-------------------------------------------------------------------------------
--  account log
-------------------------------------------------------------------------------
CREATE TABLE ACCOUNT_LOG(
        ID BIGINT NOT NULL,
	USERNAME VARCHAR(64),
	RESULT VARCHAR(64),
	REASON VARCHAR(200),
	APPLICATION VARCHAR(200),
	LOG_TIME TIMESTAMP,
        CLIENT VARCHAR(200),
	SERVER VARCHAR(200),
	DESCRIPTION VARCHAR(200),
        CONSTRAINT PK_ACCOUNT_LOG PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

