

-------------------------------------------------------------------------------
--  disk sid
-------------------------------------------------------------------------------
CREATE TABLE DISK_SID(
    ID BIGINT NOT NULL,
    CATALOG VARCHAR(50),
    VALUE VARCHAR(100),
    NAME VARCHAR(100),
    REF VARCHAR(100),
    CREATOR VARCHAR(64),
    CREATE_TIME DATETIME,
    UPDATER VARCHAR(64),
    UPDATE_TIME DATETIME,
    STATUS VARCHAR(50),

    CONSTRAINT PK_DISK_SID PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

CREATE INDEX IDX_DISK_SID_VALUE ON DISK_SID(VALUE);















