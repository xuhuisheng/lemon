

-------------------------------------------------------------------------------
--  expense request
-------------------------------------------------------------------------------
CREATE TABLE EXPENSE_REQUEST(
    ID BIGINT NOT NULL,
    CODE VARCHAR(100),
    USER_ID VARCHAR(64),
    DEPT_CODE VARCHAR(50),
    DEPT_NAME VARCHAR(200),
    CREATE_TIME DATETIME,
    STATUS VARCHAR(50),
    DESCRIPTION VARCHAR(200),
    TENANT_ID VARCHAR(64),

    TYPE VARCHAR(50),
    PRICE FLOAT,
    START_TIME DATETIME,
    END_TIME DATETIME,
    HEAD_COUNT INTEGER,
    PERSON VARCHAR(200),
    TRAFFIC VARCHAR(100),
    COUNTRY VARCHAR(100),
    ADDRESS VARCHAR(100),
    THING VARCHAR(100),

    CONSTRAINT PK_EXPENSE_REQUEST PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;
























