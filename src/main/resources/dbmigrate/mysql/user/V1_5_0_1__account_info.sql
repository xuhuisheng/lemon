

-------------------------------------------------------------------------------
--  account info
-------------------------------------------------------------------------------
CREATE TABLE ACCOUNT_INFO(
        ID BIGINT NOT NULL,
	CODE VARCHAR(50),
        USERNAME VARCHAR(50),
	TYPE VARCHAR(50),
	DISPLAY_NAME VARCHAR(200),
	STATUS VARCHAR(50),
	PASSWORD_REQUIRED VARCHAR(50),
	LOCKED VARCHAR(50),
	CREATE_TIME DATETIME,
	CLOSE_TIME DATETIME,
	LOGIN_TIME DATETIME,
	NICK_NAME VARCHAR(200),
	DESCRIPTION VARCHAR(200),
	LANGUAGE VARCHAR(50),
	TIMEZONE VARCHAR(50),
        CONSTRAINT PK_ACCOUNT_INFO PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

















