

-------------------------------------------------------------------------------
--  job grade
-------------------------------------------------------------------------------
CREATE TABLE JOB_GRADE(
        ID BIGINT auto_increment,
	NAME VARCHAR(50),
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_JOB_GRADE PRIMARY KEY(ID)
) engine=innodb;

