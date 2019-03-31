

-------------------------------------------------------------------------------
--  leave request
-------------------------------------------------------------------------------
CREATE TABLE LEAVE_REQUEST(
    ID BIGINT NOT NULL,
    CODE VARCHAR(100),
    USER_ID VARCHAR(64),
    DEPT_CODE VARCHAR(50),
    DEPT_NAME VARCHAR(200),
    CREATE_TIME DATETIME,
    STATUS VARCHAR(50),
    DESCRIPTION VARCHAR(200),
    TENANT_ID VARCHAR(64),

    TYPE VARCHAR(100),
    START_TIME DATE,
    END_TIME DATE,
    DAY INT,
    HAND_OVER_PERSON VARCHAR(200),

    CONSTRAINT PK_LEAVE_REQUEST PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;



















