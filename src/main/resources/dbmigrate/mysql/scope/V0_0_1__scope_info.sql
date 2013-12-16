

-------------------------------------------------------------------------------
--  scope info
-------------------------------------------------------------------------------
CREATE TABLE SCOPE_INFO(
        ID BIGINT auto_increment,
        NAME VARCHAR(50),
	CODE VARCHAR(50),
	REF VARCHAR(50),
	SHARED INTEGER,
	USER_REPO_REF VARCHAR(50),
        CONSTRAINT PK_SCOPE_INFO PRIMARY KEY(ID)
) engine=innodb;

