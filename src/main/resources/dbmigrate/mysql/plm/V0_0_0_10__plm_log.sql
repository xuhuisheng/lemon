

-------------------------------------------------------------------------------
--  plm log
-------------------------------------------------------------------------------
CREATE TABLE PLM_LOG(
        ID BIGINT NOT NULL,
	TYPE VARCHAR(50),
	USER_ID VARCHAR(64),
	LOG_TIME DATETIME,
	CONTENT TEXT,
	ISSUE_ID BIGINT,
	CONSTRAINT PK_PLM_LOG PRIMARY KEY(ID),
        CONSTRAINT FK_PIM_LOG_ISSUE FOREIGN KEY(ISSUE_ID) REFERENCES PLM_ISSUE(ID)
) ENGINE=INNODB CHARSET=UTF8;

