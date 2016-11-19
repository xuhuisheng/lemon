

-------------------------------------------------------------------------------
--  task def base
-------------------------------------------------------------------------------
CREATE TABLE TASK_DEF_BASE(
	ID BIGINT NOT NULL,
	CODE VARCHAR(100),
	NAME VARCHAR(200),
	PROCESS_DEFINITION_ID VARCHAR(200),
	FORM_KEY VARCHAR(200),
	FORM_TYPE VARCHAR(50),
	COUNTERSIGN_TYPE VARCHAR(50),
	COUNTERSIGN_USER VARCHAR(200),
	COUNTERSIGN_STRATEGY VARCHAR(50),
	COUNTERSIGN_RATE INT,
	ASSIGN_STRATEGY VARCHAR(100),
        CONSTRAINT PK_TASK_DEF_BASE PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;















