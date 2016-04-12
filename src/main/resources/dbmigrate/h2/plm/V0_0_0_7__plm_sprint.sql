

-------------------------------------------------------------------------------
--  plm sprint
-------------------------------------------------------------------------------
CREATE TABLE PLM_SPRINT(
        ID BIGINT NOT NULL,
	CODE VARCHAR(50),
	NAME VARCHAR(200),
	PRIORITY INT,
	START_TIME DATETIME,
	END_TIME DATETIME,
	STATUS VARCHAR(50),
	CONFIG_ID BIGINT,
	PROJECT_ID BIGINT,
	CONSTRAINT PK_PLM_SPRINT PRIMARY KEY(ID),
        CONSTRAINT FK_PIM_SPRINT_CONFIG FOREIGN KEY(CONFIG_ID) REFERENCES PLM_CONFIG(ID),
        CONSTRAINT FK_PIM_SPRINT_PROJECT FOREIGN KEY(PROJECT_ID) REFERENCES PLM_PROJECT(ID)
);

