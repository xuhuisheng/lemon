

-------------------------------------------------------------------------------
--  work report acl
-------------------------------------------------------------------------------
CREATE TABLE WORK_REPORT_ACL(
        ID BIGINT NOT NULL,
	TYPE VARCHAR(200),
	REF VARCHAR(200),
	TENANT_ID VARCHAR(64),
	INFO_ID BIGINT,
        CONSTRAINT PK_WORK_REPORT_ACL PRIMARY KEY(ID),
	CONSTRAINT FK_WORK_REPORT_ACL_INFO FOREIGN KEY(INFO_ID) REFERENCES WORK_REPORT_INFO(ID)
);

