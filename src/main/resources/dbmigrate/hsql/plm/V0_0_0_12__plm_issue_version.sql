

-------------------------------------------------------------------------------
--  plm issue version
-------------------------------------------------------------------------------
CREATE TABLE PLM_ISSUE_VERSION(
	ISSUE_ID BIGINT,
	VERSION_ID BIGINT,
	CONSTRAINT PK_PLM_ISSUE_VERSION PRIMARY KEY(ISSUE_ID,VERSION_ID),
        CONSTRAINT FK_PIM_ISSUE_VERSION_ISSUE FOREIGN KEY(ISSUE_ID) REFERENCES PLM_ISSUE(ID),
        CONSTRAINT FK_PIM_ISSUE_VERSION_VERSION FOREIGN KEY(VERSION_ID) REFERENCES PLM_VERSION(ID)
);

