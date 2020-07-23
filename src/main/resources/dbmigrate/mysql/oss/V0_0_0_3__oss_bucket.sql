

-------------------------------------------------------------------------------
--  oss bucket
-------------------------------------------------------------------------------
CREATE TABLE OSS_BUCKET(
    ID BIGINT NOT NULL,
    CODE VARCHAR(50),
    NAME VARCHAR(200),
    STATUS VARCHAR(50),
    CREATE_TIME TIMESTAMP,
    DESCRIPTION VARCHAR(200),
    USER_ID VARCHAR(64),
    TENANT_ID VARCHAR(50),
    REGION_ID BIGINT,
    CONSTRAINT PK_OSS_BUCKET PRIMARY KEY(ID),
    CONSTRAINT FK_OSS_BUCKET_REGION FOREIGN KEY(REGION_ID) REFERENCES OSS_REGION(ID)
) ENGINE=INNODB CHARSET=UTF8;
