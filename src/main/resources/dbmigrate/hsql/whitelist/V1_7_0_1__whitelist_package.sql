

-------------------------------------------------------------------------------
--  whitelist package
-------------------------------------------------------------------------------
CREATE TABLE WHITELIST_PACKAGE(
        ID BIGINT NOT NULL,
	NAME VARCHAR(50),
	DESCRIPTION VARCHAR(200),
	USER_ID VARCHAR(64),
	CREATE_TIME DATETIME,
	UPDATE_TIME DATETIME,
	STATUS VARCHAR(50),
	PRIORITY VARCHAR(50),

        CONSTRAINT PK_WHITELIST_PACKAGE PRIMARY KEY(ID)
);

