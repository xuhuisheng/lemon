

-------------------------------------------------------------------------------
--  scope info
-------------------------------------------------------------------------------
CREATE TABLE SCOPE_INFO(
        ID BIGINT AUTO_INCREMENT,
        NAME VARCHAR(50),
	CODE VARCHAR(50),
	REF VARCHAR(50),
	SHARED INTEGER,
	USER_REPO_REF VARCHAR(50),
        CONSTRAINT PK_SCOPE_INFO PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

