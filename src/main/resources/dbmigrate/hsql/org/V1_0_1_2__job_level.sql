

-------------------------------------------------------------------------------
--  job level
-------------------------------------------------------------------------------
CREATE TABLE JOB_LEVEL(
        ID BIGINT NOT NULL,
	NAME VARCHAR(50),
	GRADE_ID BIGINT,
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_JOB_LEVEL PRIMARY KEY(ID),
        CONSTRAINT FK_JOB_LEVEL_GRADE FOREIGN KEY(GRADE_ID) REFERENCES JOB_GRADE(ID)
);

