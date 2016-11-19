

-------------------------------------------------------------------------------
--  user account
-------------------------------------------------------------------------------
CREATE TABLE USER_ACCOUNT(
        ID BIGINT NOT NULL,
	USERNAME VARCHAR(50),
	PASSWORD VARCHAR(50),
	SCOPE_ID VARCHAR(50),
	USER_BASE_ID BIGINT,
	TYPE_ID BIGINT,
        CONSTRAINT PK_USER_ACCOUNT PRIMARY KEY(ID),
        CONSTRAINT FK_USER_ACCOUNT_TYPE FOREIGN KEY(TYPE_ID) REFERENCES USER_ACCOUNT_TYPE(ID),
        CONSTRAINT FK_USER_ACCOUNT_USER_BASE FOREIGN KEY(USER_BASE_ID) REFERENCES USER_BASE(ID)
) ENGINE=INNODB CHARSET=UTF8;

