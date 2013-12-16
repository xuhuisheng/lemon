

-------------------------------------------------------------------------------
--  role def
-------------------------------------------------------------------------------
CREATE TABLE AUTH_ROLE_DEF(
        ID BIGINT auto_increment,
        NAME VARCHAR(50),
        DESCN VARCHAR(200),
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_AUTH_ROLE_DEF PRIMARY KEY(ID)
) engine=innodb;
