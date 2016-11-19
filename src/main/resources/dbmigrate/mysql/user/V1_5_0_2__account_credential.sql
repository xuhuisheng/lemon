

-------------------------------------------------------------------------------
--  account credential
-------------------------------------------------------------------------------
CREATE TABLE ACCOUNT_CREDENTIAL(
        ID BIGINT NOT NULL,
	PASSWORD VARCHAR(50),
        MODIFY_TIME DATETIME,
	EXPIRE_TIME DATETIME,
	EXPIRE_STATUS VARCHAR(50),
	REQUIRED VARCHAR(50),
	COULD_MODIFY VARCHAR(50),
	TYPE VARCHAR(50),
	CATALOG VARCHAR(50),
	DATA VARCHAR(200),
	STATUS VARCHAR(50),
	ACCOUNT_ID BIGINT,
        CONSTRAINT PK_ACCOUNT_CREDENTIAL PRIMARY KEY(ID),
        CONSTRAINT FK_ACCOUNT_CREDENTIAL_ACCOUNT FOREIGN KEY(ACCOUNT_ID) REFERENCES ACCOUNT_INFO(ID)
) ENGINE=INNODB CHARSET=UTF8;















