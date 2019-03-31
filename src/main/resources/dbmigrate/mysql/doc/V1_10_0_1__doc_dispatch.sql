

-------------------------------------------------------------------------------
--  doc dispatch
-------------------------------------------------------------------------------
CREATE TABLE DOC_DISPATCH(
    ID BIGINT NOT NULL,
    CODE VARCHAR(100),
    USER_ID VARCHAR(64),
    DEPT_CODE VARCHAR(50),
    DEPT_NAME VARCHAR(200),
    CREATE_TIME DATETIME,
    STATUS VARCHAR(50),
    DESCRIPTION VARCHAR(200),
    TENANT_ID VARCHAR(64),

    TITLE VARCHAR(50),
    DISPATCH_DATE DATE,
    SCOPE VARCHAR(200),
    CONTENT VARCHAR(200),
    ATTACHMENT VARCHAR(100),

    CONSTRAINT PK_DOC_DISPATCH PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;



















