

-------------------------------------------------------------------------------
--  disk space
-------------------------------------------------------------------------------
CREATE TABLE DISK_SPACE(
    ID BIGINT NOT NULL,
    CATALOG VARCHAR(50),
    TYPE VARCHAR(50),
    NAME VARCHAR(200),
    DESCRIPTION VARCHAR(255),
    CREATOR VARCHAR(64),
    CREATE_TIME DATETIME,
    STATUS VARCHAR(50),
    PRIORITY INT,
    QUOTA INT,
    CONSTRAINT PK_DISK_SPACE PRIMARY KEY(ID)
);



