

-------------------------------------------------------------------------------
--  disk file
-------------------------------------------------------------------------------
CREATE TABLE DISK_FILE(
    ID BIGINT NOT NULL,
    NAME VARCHAR(200),
    FILE_SIZE BIGINT,
    VALUE VARCHAR(200),
    TYPE VARCHAR(10),
    PART_INDEX INT,
    FILE_CODE BIGINT,
    REF_COUNT INT,
    HASH_CODE VARCHAR(64),
    CREATOR VARCHAR(64),
    CREATE_TIME DATETIME,
    UPDATER VARCHAR(64),
    UPDATE_TIME DATETIME,
    STATUS VARCHAR(50),
    USER_ID VARCHAR(64),
    FOLDER_PATH VARCHAR(200),

    CONSTRAINT PK_DISK_FILE PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

CREATE INDEX IDX_DISK_FILE_HASHCODE ON DISK_FILE(HASH_CODE);





















