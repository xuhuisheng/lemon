

-------------------------------------------------------------------------------
--  access
-------------------------------------------------------------------------------
CREATE TABLE AUTH_ACCESS(
        ID BIGINT NOT NULL,
	TYPE VARCHAR(50),
	VALUE VARCHAR(200),
	PERM_ID BIGINT,
	PRIORITY INTEGER,
	DESCN VARCHAR(200),
	SCOPE_ID VARCHAR(50),
	CONSTRAINT PK_AUTH_ACCESS PRIMARY KEY(ID),
	CONSTRAINT FK_AUTH_ACCESS_PERM FOREIGN KEY(PERM_ID) REFERENCES AUTH_PERM(ID)
) ENGINE=INNODB CHARSET=UTF8;









