

-------------------------------------------------------------------------------
--  permission
-------------------------------------------------------------------------------
CREATE TABLE AUTH_PERM(
        ID BIGINT NOT NULL,
	CODE VARCHAR(200),
	NAME VARCHAR(200),
	PERM_TYPE_ID BIGINT NOT NULL,
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_AUTH_PERM PRIMARY KEY(ID),
	CONSTRAINT FK_AUTH_PERM_TYPE FOREIGN KEY(PERM_TYPE_ID) REFERENCES AUTH_PERM_TYPE(ID)
) ENGINE=INNODB CHARSET=UTF8;







