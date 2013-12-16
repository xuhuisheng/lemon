

-------------------------------------------------------------------------------
--  role
-------------------------------------------------------------------------------
CREATE TABLE AUTH_ROLE(
        ID BIGINT auto_increment,
        NAME VARCHAR(50),
        DESCN VARCHAR(200),
	ROLE_DEF_ID BIGINT,
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_AUTH_ROLE PRIMARY KEY(ID),
        CONSTRAINT FK_AUTH_ROLE_DEF FOREIGN KEY(ROLE_DEF_ID) REFERENCES AUTH_ROLE_DEF(ID)
) engine=innodb;
