

-------------------------------------------------------------------------------
--  doc incoming
-------------------------------------------------------------------------------
CREATE TABLE DOC_INCOMING(
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
    INCOMING_DATE DATE,
    SCOPE VARCHAR(200),
    CONTENT VARCHAR(200),
    ATTACHMENT VARCHAR(100),

    CONSTRAINT PK_DOC_INCOMING PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;



















