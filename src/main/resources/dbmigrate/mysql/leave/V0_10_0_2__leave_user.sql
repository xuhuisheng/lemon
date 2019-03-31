

-------------------------------------------------------------------------------
--  leave user
-------------------------------------------------------------------------------
CREATE TABLE LEAVE_USER(
    ID BIGINT NOT NULL,
    CODE VARCHAR(50),
    NAME VARCHAR(200),
    TYPE VARCHAR(50),
    CREATE_TIME DATETIME,
    UPDATE_TIME DATETIME,
    STATUS VARCHAR(50),
    DESCRIPTION VARCHAR(200),
    USER_ID VARCHAR(64),
    RULE_ID BIGINT,
    TENANT_ID VARCHAR(64),
    CONSTRAINT PK_LEAVE_USER PRIMARY KEY(ID),
    CONSTRAINT FK_LEAVE_USER_RULE FOREIGN KEY(RULE_ID) REFERENCES LEAVE_RULE(ID)
) ENGINE=INNODB CHARSET=UTF8;

