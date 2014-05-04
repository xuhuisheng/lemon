

-------------------------------------------------------------------------------
--  job title
-------------------------------------------------------------------------------
CREATE TABLE JOB_TITLE(
        ID BIGINT auto_increment,
	NAME VARCHAR(50),
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_JOB_TITLE PRIMARY KEY(ID)
) engine=innodb;

