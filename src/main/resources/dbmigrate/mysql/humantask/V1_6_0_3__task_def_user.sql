

-------------------------------------------------------------------------------
--  task def user
-------------------------------------------------------------------------------
CREATE TABLE TASK_DEF_USER(
	ID BIGINT NOT NULL,
	VALUE VARCHAR(200),
	TYPE VARCHAR(50),
	CATALOG VARCHAR(200),
	BASE_ID BIGINT,
        CONSTRAINT PK_TASK_DEF_USER PRIMARY KEY(ID),
        CONSTRAINT FK_TASK_DEF_USER_BASE FOREIGN KEY(BASE_ID) REFERENCES TASK_DEF_BASE(ID)
) ENGINE=INNODB CHARSET=UTF8;









