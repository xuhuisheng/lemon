

-------------------------------------------------------------------------------
--  oss endpoint
-------------------------------------------------------------------------------
CREATE TABLE OSS_ENDPOINT(
    ID BIGINT NOT NULL,
    TYPE VARCHAR(50),
    CODE VARCHAR(50),
    NAME VARCHAR(200),
    STATUS VARCHAR(50),
    CREATE_TIME TIMESTAMP,
    DESCRIPTION VARCHAR(200),
    TENANT_ID VARCHAR(50),
    REGION_ID BIGINT,
    CONSTRAINT PK_OSS_ENDPOINT PRIMARY KEY(ID),
    CONSTRAINT FK_OSS_ENDPOINT_REGION FOREIGN KEY(REGION_ID) REFERENCES OSS_REGION(ID)
) ENGINE=INNODB CHARSET=UTF8;
