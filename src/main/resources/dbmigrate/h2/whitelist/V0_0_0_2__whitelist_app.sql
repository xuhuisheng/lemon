

-------------------------------------------------------------------------------
--  whitelist app
-------------------------------------------------------------------------------
CREATE TABLE WHITELIST_APP(
        ID BIGINT NOT NULL,
	NAME VARCHAR(50),
	TYPE_ID BIGINT,
	USER_ID VARCHAR(50),
	DESCRIPTION VARCHAR(200),
	FORCE_RELOGIN INT,
	CODE VARCHAR(64),
	USERNAME VARCHAR(64),
	PASSWORD VARCHAR(200),
	LEVEL INT,
        CONSTRAINT PK_WHITELIST_APP PRIMARY KEY(ID),
        CONSTRAINT FK_WHITELIST_APP_TYPE FOREIGN KEY(TYPE_ID) REFERENCES WHITELIST_TYPE(ID)
);

