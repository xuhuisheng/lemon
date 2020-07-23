

-------------------------------------------------------------------------------
--  sys entry
-------------------------------------------------------------------------------
CREATE TABLE SYS_ENTRY(
    ID BIGINT NOT NULL,
    LOGO VARCHAR(200),
    TYPE VARCHAR(50),
    CODE VARCHAR(50),
    NAME VARCHAR(50),
    URL VARCHAR(200),
    PRIORITY INTEGER,
    STATUS VARCHAR(50),
    DESCN VARCHAR(200),
    CATEGORY_ID BIGINT,
    PLATFORM VARCHAR(50),
    CREATE_TIME TIMESTAMP,
    USER_ID VARCHAR(64),
    SYS_ID BIGINT,

    TENANT_ID VARCHAR(64),

    CONSTRAINT PK_SYS_ENTRY PRIMARY KEY(ID),
    CONSTRAINT FK_SYS_ENTRY_SYS FOREIGN KEY(SYS_ID) REFERENCES SYS_INFO(ID),
    CONSTRAINT FK_SYS_ENTRY_CATEGORY FOREIGN KEY(CATEGORY_ID) REFERENCES SYS_CATEGORY(ID)
) ENGINE=INNODB CHARSET=UTF8;



















