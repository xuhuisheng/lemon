

-------------------------------------------------------------------------------
--  permission type
-------------------------------------------------------------------------------
CREATE TABLE AUTH_PERM_TYPE(
        ID BIGINT auto_increment,
        NAME VARCHAR(50),
	TYPE INTEGER,
	PRIORITY INTEGER,
        DESCN VARCHAR(200),
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_AUTH_PERM_TYPE PRIMARY KEY(ID)
) engine=innodb;
