

-------------------------------------------------------------------------------
--  disk rule
-------------------------------------------------------------------------------
CREATE TABLE DISK_RULE(
    ID BIGINT NOT NULL,
    CREATE_TIME TIMESTAMP,
    USER_ID VARCHAR(64),
    UPDATE_TIME TIMESTAMP,
    CONSTRAINT PK_DISK_RULE PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

