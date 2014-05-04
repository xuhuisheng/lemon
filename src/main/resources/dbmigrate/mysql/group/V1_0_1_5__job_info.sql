

-------------------------------------------------------------------------------
--  job info
-------------------------------------------------------------------------------
CREATE TABLE JOB_INFO(
        ID BIGINT auto_increment,
	NAME VARCHAR(50),
	LEVEL_ID BIGINT,
	TYPE_ID BIGINT,
	TITLE_ID BIGINT,
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_JOB_INFO PRIMARY KEY(ID),
        CONSTRAINT FK_JOB_INFO_LEVEL FOREIGN KEY(LEVEL_ID) REFERENCES JOB_LEVEL(ID),
        CONSTRAINT FK_JOB_INFO_TYPE FOREIGN KEY(TYPE_ID) REFERENCES JOB_TYPE(ID),
        CONSTRAINT FK_JOB_INFO_TITLE FOREIGN KEY(TITLE_ID) REFERENCES JOB_TITLE(ID)
) engine=innodb;

