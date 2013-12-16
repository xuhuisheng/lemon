

-------------------------------------------------------------------------------
--  user status
-------------------------------------------------------------------------------
CREATE TABLE AUTH_USER_STATUS(
	ID BIGINT auto_increment,
	USERNAME VARCHAR(50),
	PASSWORD VARCHAR(50),
	STATUS INTEGER,
	REFERENCE VARCHAR(200),
	USER_REPO_REF VARCHAR(50),
	SCOPE_ID VARCHAR(50),
	CONSTRAINT PK_AUTH_USER_STATUS PRIMARY KEY(ID)
) engine=innodb;
