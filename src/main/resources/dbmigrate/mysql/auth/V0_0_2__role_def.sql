

-------------------------------------------------------------------------------
--  role def
-------------------------------------------------------------------------------
CREATE TABLE AUTH_ROLE_DEF(
        ID BIGINT AUTO_INCREMENT,
        NAME VARCHAR(50),
        DESCN VARCHAR(200),
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_AUTH_ROLE_DEF PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;
