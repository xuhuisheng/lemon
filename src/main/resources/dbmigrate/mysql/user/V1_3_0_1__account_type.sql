

-------------------------------------------------------------------------------
--  user account type
-------------------------------------------------------------------------------
CREATE TABLE USER_ACCOUNT_TYPE(
        ID BIGINT NOT NULL,
        NAME VARCHAR(50),
	DESCRIPTION VARCHAR(200),
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_USER_ACCOUNT_TYPE PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

