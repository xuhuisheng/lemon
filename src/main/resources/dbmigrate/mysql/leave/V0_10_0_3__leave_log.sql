

-------------------------------------------------------------------------------
--  leave log
-------------------------------------------------------------------------------
CREATE TABLE LEAVE_LOG(
    ID BIGINT NOT NULL,
    NAME VARCHAR(200),
    VALUE VARCHAR(200),
    CREATE_TIME DATETIME,
    STATUS VARCHAR(50),
    DESCRIPTION VARCHAR(200),
    USER_ID BIGINT,
    TENANT_ID VARCHAR(64),
    CONSTRAINT PK_LEAVE_LOG PRIMARY KEY(ID),
    CONSTRAINT FK_LEAVE_LOG_USER FOREIGN KEY(USER_ID) REFERENCES LEAVE_USER(ID)
) ENGINE=INNODB CHARSET=UTF8;

