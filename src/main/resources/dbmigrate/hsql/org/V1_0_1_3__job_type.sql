

-------------------------------------------------------------------------------
--  job type
-------------------------------------------------------------------------------
CREATE TABLE JOB_TYPE(
        ID BIGINT NOT NULL,
	NAME VARCHAR(50),
	PARENT_ID BIGINT,
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_JOB_TYPE PRIMARY KEY(ID),
        CONSTRAINT FK_JOB_TYPE_PARENT FOREIGN KEY(PARENT_ID) REFERENCES JOB_TYPE(ID)
);

