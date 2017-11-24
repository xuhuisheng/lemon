

-------------------------------------------------------------------------------
--  account alias
-------------------------------------------------------------------------------
CREATE TABLE ACCOUNT_ALIAS(
        ID BIGINT NOT NULL,
	NAME VARCHAR(100),
	TYPE VARCHAR(50),
	CREATE_TIME DATETIME,
	DESCN VARCHAR(200),
	ACCOUNT_ID BIGINT,
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_ACCOUNT_ALIAS PRIMARY KEY(ID),
        CONSTRAINT FK_ACCOUNT_ALIAS_ACCOUNT FOREIGN KEY(ACCOUNT_ID) REFERENCES ACCOUNT_INFO(ID)
);

