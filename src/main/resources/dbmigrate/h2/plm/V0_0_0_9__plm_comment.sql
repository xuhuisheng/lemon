

-------------------------------------------------------------------------------
--  plm comment
-------------------------------------------------------------------------------
CREATE TABLE PLM_COMMENT(
        ID BIGINT NOT NULL,
	CONTENT VARCHAR(65535),
	CREATE_TIME DATETIME,
	USER_ID VARCHAR(64),
	PRIORITY INT,
	ISSUE_ID BIGINT,
	CONSTRAINT PK_PLM_COMMENT PRIMARY KEY(ID),
        CONSTRAINT FK_PIM_COMMENT_ISSUE FOREIGN KEY(ISSUE_ID) REFERENCES PLM_ISSUE(ID)
);

