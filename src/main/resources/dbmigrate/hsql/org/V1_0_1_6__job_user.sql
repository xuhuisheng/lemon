

-------------------------------------------------------------------------------
--  job user
-------------------------------------------------------------------------------
CREATE TABLE JOB_USER(
        ID BIGINT NOT NULL,
	USER_REF VARCHAR(50),
	JOB_INFO_ID BIGINT,
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_JOB_USER PRIMARY KEY(ID),
        CONSTRAINT FK_JOB_USER_JOB FOREIGN KEY(JOB_INFO_ID) REFERENCES JOB_INFO(ID)
);

