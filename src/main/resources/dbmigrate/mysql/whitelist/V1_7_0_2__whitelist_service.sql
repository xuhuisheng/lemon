

-------------------------------------------------------------------------------
--  whitelist service
-------------------------------------------------------------------------------
CREATE TABLE WHITELIST_SERVICE(
        ID BIGINT NOT NULL,
	NAME VARCHAR(50),
	DESCRIPTION VARCHAR(200),
	USER_ID VARCHAR(64),
	CREATE_TIME DATETIME,
	UPDATE_TIME DATETIME,
	STATUS VARCHAR(50),
	PRIORITY VARCHAR(50),

	PACKAGE_ID BIGINT,

        CONSTRAINT PK_WHITELIST_SERVICE PRIMARY KEY(ID),
        CONSTRAINT FK_WHITELIST_S_PACKAGE FOREIGN KEY(PACKAGE_ID) REFERENCES WHITELIST_PACKAGE(ID)
) ENGINE=INNODB CHARSET=UTF8;

