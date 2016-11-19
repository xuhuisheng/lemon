

-------------------------------------------------------------------------------
--  work report attachment
-------------------------------------------------------------------------------
CREATE TABLE WORK_REPORT_ATTACHMENT(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	FILE_SIZE BIGINT,
	REF VARCHAR(200),
	TENANT_ID VARCHAR(64),
	INFO_ID BIGINT,
        CONSTRAINT PK_WORK_REPORT_ATTACHMENT PRIMARY KEY(ID),
	CONSTRAINT FK_WORK_REPORT_ATTACHMENT_INFO FOREIGN KEY(INFO_ID) REFERENCES WORK_REPORT_INFO(ID)
);

