

-------------------------------------------------------------------------------
--  account history credential
-------------------------------------------------------------------------------
CREATE TABLE ACCOUNT_HISTORY_CREDENTIAL(
        ID BIGINT NOT NULL,
	MODIFY_TIME DATETIME,
	PASSWORD VARCHAR(100),
	CREDENTIAL_ID BIGINT,
        CONSTRAINT PK_ACCOUNT_HISTORY_CREDENTIAL PRIMARY KEY(ID),
        CONSTRAINT FK_ACCOUNT_HISTORY_CREDENTIAL_CREDENTIAL FOREIGN KEY(CREDENTIAL_ID) REFERENCES ACCOUNT_CREDENTIAL(ID)
);

