

-------------------------------------------------------------------------------
--  oss data
-------------------------------------------------------------------------------
CREATE TABLE OSS_DATA(
    ID BIGINT NOT NULL,
    CODE VARCHAR(200),
    DATA BLOB,
    STATUS VARCHAR(50),
    CREATE_TIME TIMESTAMP,
    DESCRIPTION VARCHAR(200),
    USER_ID VARCHAR(64),
    TENANT_ID VARCHAR(50),
    CONSTRAINT PK_OSS_DATA PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;










