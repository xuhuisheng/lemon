

-------------------------------------------------------------------------------
--  task def escalation
-------------------------------------------------------------------------------
-- notification, reassignment, operation
CREATE TABLE TASK_DEF_ESCALATION(
	ID BIGINT NOT NULL,
	TYPE VARCHAR(50),
	STATUS VARCHAR(50),
	ESCALATION_CONDITION VARCHAR(200),
	VALUE VARCHAR(200),
	DEADLINE_ID BIGINT,
        CONSTRAINT PK_TASK_DEF_ESCALATION PRIMARY KEY(ID),
        CONSTRAINT FK_TASK_DEF_ESCALATION_DEADLINE FOREIGN KEY(DEADLINE_ID) REFERENCES TASK_DEF_DEADLINE(ID)
) ENGINE=INNODB CHARSET=UTF8;










