

-------------------------------------------------------------------------------
--  task participant
-------------------------------------------------------------------------------
CREATE TABLE TASK_PARTICIPANT(
	ID BIGINT NOT NULL,
	CATEGORY VARCHAR(200),
	TYPE VARCHAR(200),
	REF VARCHAR(200),
	TASK_ID BIGINT,
        CONSTRAINT PK_TASK_PARTICIPANT PRIMARY KEY(ID),
	CONSTRAINT FK_TASK_PARTICIPANT_TASK FOREIGN KEY(TASK_ID) REFERENCES TASK_INFO(ID)
) ENGINE=INNODB CHARSET=UTF8;













