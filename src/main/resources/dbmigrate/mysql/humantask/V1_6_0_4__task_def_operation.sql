

-------------------------------------------------------------------------------
--  task def operation
-------------------------------------------------------------------------------
CREATE TABLE TASK_DEF_OPERATION(
	ID BIGINT NOT NULL,
	VALUE VARCHAR(200),
	TYPE VARCHAR(50),
	STATUS VARCHAR(50),
	PRIORITY INT,
	BASE_ID BIGINT,
        CONSTRAINT PK_TASK_DEF_OPERATION PRIMARY KEY(ID),
        CONSTRAINT FK_TASK_DEF_OPERATION_BASE FOREIGN KEY(BASE_ID) REFERENCES TASK_DEF_BASE(ID)
) ENGINE=INNODB CHARSET=UTF8;










