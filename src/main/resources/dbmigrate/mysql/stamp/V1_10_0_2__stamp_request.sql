

-------------------------------------------------------------------------------
--  stamp request
-------------------------------------------------------------------------------
CREATE TABLE STAMP_REQUEST(
    ID BIGINT NOT NULL,
    CODE VARCHAR(100),
    USER_ID VARCHAR(64),
    DEPT_CODE VARCHAR(50),
    DEPT_NAME VARCHAR(200),
    CREATE_TIME DATETIME,
    STATUS VARCHAR(50),
    DESCRIPTION VARCHAR(200),
    TENANT_ID VARCHAR(64),

    STAMP_DEPT_CODE VARCHAR(50),
    STAMP_DEPT_NAME VARCHAR(200),
    STAMP_DATE DATE,
    FILE_NAME VARCHAR(100),
    FILE_PATH VARCHAR(200),
    FILE_COPY INT,
    FILE_PAGE INT,
    FILE_COMMENT VARCHAR(200),
    COMPANY_CODE VARCHAR(50),
    COMPANY_NAME VARCHAR(100),
    TYPE VARCHAR(50),
    ACCEPT_UNIT VARCHAR(100),

    CONSTRAINT PK_STAMP_REQUEST PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

























