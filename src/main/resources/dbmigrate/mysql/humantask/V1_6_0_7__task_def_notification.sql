

-------------------------------------------------------------------------------
--  task def notification
-------------------------------------------------------------------------------
CREATE TABLE TASK_DEF_NOTIFICATION(
	ID BIGINT NOT NULL,
	EVENT_NAME VARCHAR(100),
	RECEIVER VARCHAR(200),
	TYPE VARCHAR(50),
	TEMPLATE_CODE VARCHAR(200),
	BASE_ID BIGINT,
        CONSTRAINT PK_TASK_DEF_NOTIFICATION PRIMARY KEY(ID),
        CONSTRAINT FK_TASK_DEF_NOTIFICATION_BASE FOREIGN KEY(BASE_ID) REFERENCES TASK_DEF_BASE(ID)
) ENGINE=INNODB CHARSET=UTF8;










