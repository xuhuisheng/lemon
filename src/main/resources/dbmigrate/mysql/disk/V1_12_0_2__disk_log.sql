

-------------------------------------------------------------------------------
--  disk log
-------------------------------------------------------------------------------
CREATE TABLE DISK_LOG(
    ID BIGINT NOT NULL,
    NAME VARCHAR(200),
    DESCRIPTION VARCHAR(255),

    TYPE VARCHAR(50),
    SOURCE_ID BIGINT,
    PARENT_ID BIGINT,
    SPACE_ID BIGINT,

    CATALOG VARCHAR(50),
    OLD_VALUE VARCHAR(200),
    NEW_VALUE VARCHAR(200),

    CREATOR VARCHAR(64),
    CREATE_TIME DATETIME,
    UPDATER VARCHAR(64),
    UPDATE_TIME DATETIME,

    STATUS VARCHAR(50),
    REF_TYPE VARCHAR(50),
    REF_VALUE VARCHAR(200),

    CONSTRAINT PK_DISK_LOG PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;




















