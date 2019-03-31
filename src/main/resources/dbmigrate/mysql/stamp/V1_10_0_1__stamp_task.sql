

-------------------------------------------------------------------------------
--  stamp task
-------------------------------------------------------------------------------
CREATE TABLE STAMP_TASK(
    ID BIGINT NOT NULL,
    NAME VARCHAR(200),
    USER_ID VARCHAR(64),
    CREATE_TIME DATETIME,
    STATUS VARCHAR(50),
    CONTENT VARCHAR(200),
    DESCRIPTION VARCHAR(200),
    TENANT_ID VARCHAR(64),
    INFO_ID BIGINT,
    CONSTRAINT PK_STAMP_TASK PRIMARY KEY(ID),
    CONSTRAINT FK_STAMP_TASK_INFO FOREIGN KEY(INFO_ID) REFERENCES STAMP_INFO(ID)
) ENGINE=INNODB CHARSET=UTF8;












