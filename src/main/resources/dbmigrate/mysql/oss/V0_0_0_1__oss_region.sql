

-------------------------------------------------------------------------------
--  oss region
-------------------------------------------------------------------------------
CREATE TABLE OSS_REGION(
    ID BIGINT NOT NULL,
    CODE VARCHAR(50),
    NAME VARCHAR(200),
    STATUS VARCHAR(50),
    CREATE_TIME TIMESTAMP,
    DESCRIPTION VARCHAR(200),
    TENANT_ID VARCHAR(50),
    CONSTRAINT PK_OSS_REGION PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;
