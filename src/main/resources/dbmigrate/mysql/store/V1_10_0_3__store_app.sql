

-------------------------------------------------------------------------------
--  store app
-------------------------------------------------------------------------------
CREATE TABLE STORE_APP(
    ID BIGINT NOT NULL,
    NAME VARCHAR(50),
	GROUP_NAME VARCHAR(200),
	MODEL_CODE VARCHAR(50),
	PRIORITY INT,
	APP_ID VARCHAR(200),
	APP_KEY VARCHAR(200),
	STATUS VARCHAR(50),
	DESCRIPTION VARCHAR(200),
    CONSTRAINT PK_STORE_APP PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;
