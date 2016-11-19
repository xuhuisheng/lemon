

-------------------------------------------------------------------------------
--  plm version
-------------------------------------------------------------------------------
CREATE TABLE PLM_VERSION(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	STATUS VARCHAR(50),
	CREATE_TIME DATETIME,
	USER_ID VARCHAR(64),
	PRIORITY INT,
	PROJECT_ID BIGINT,
	CONSTRAINT PK_PLM_VERSION PRIMARY KEY(ID),
        CONSTRAINT FK_PIM_VERSION_PROJECT FOREIGN KEY(PROJECT_ID) REFERENCES PLM_PROJECT(ID)
);

