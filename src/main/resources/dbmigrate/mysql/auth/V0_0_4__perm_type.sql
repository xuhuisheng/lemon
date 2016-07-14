

-------------------------------------------------------------------------------
--  permission type
-------------------------------------------------------------------------------
CREATE TABLE AUTH_PERM_TYPE(
        ID BIGINT NOT NULL,
        NAME VARCHAR(50),
	TYPE INTEGER,
	PRIORITY INTEGER,
        DESCN VARCHAR(200),
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_AUTH_PERM_TYPE PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;








