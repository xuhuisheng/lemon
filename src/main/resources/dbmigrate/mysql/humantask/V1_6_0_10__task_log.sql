

-------------------------------------------------------------------------------
--  task log
-------------------------------------------------------------------------------
CREATE TABLE TASK_LOG(
	ID BIGINT NOT NULL,

	EVENT_TYPE VARCHAR(100),
	EVENT_TIME DATETIME,
	CREATOR VARCHAR(64),
	START_OWNER VARCHAR(64),
	END_OWNER VARCHAR(64),
	TASK_STATUS VARCHAR(50),
	CONTENT TEXT,
	PRIORITY INT,

	TASK_ID BIGINT,

        CONSTRAINT PK_TASK_LOG PRIMARY KEY(ID),
	CONSTRAINT FK_TASK_LOG_TASK  FOREIGN KEY(TASK_ID) REFERENCES TASK_INFO(ID)
) ENGINE=INNODB CHARSET=UTF8;














