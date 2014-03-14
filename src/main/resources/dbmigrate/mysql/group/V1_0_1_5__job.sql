

-------------------------------------------------------------------------------
--  job
-------------------------------------------------------------------------------
CREATE TABLE JOB(
        ID BIGINT auto_increment,
	NAME VARCHAR(50),
	LEVEL_ID BIGINT,
	TYPE_ID BIGINT,
	TITLE_ID BIGINT,
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_JOB PRIMARY KEY(ID),
        CONSTRAINT FK_JOB_LEVEL FOREIGN KEY(LEVEL_ID) REFERENCES JOB_LEVEL(ID),
        CONSTRAINT FK_JOB_TYPE FOREIGN KEY(TYPE_ID) REFERENCES JOB_TYPE(ID),
        CONSTRAINT FK_JOB_TITLE FOREIGN KEY(TITLE_ID) REFERENCES JOB_TITLE(ID)
) engine=innodb;

