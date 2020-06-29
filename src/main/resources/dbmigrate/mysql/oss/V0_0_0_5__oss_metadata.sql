

-------------------------------------------------------------------------------
--  oss metadata
-------------------------------------------------------------------------------
CREATE TABLE OSS_METADATA(
    ID BIGINT NOT NULL,
    CODE VARCHAR(50),
    NAME VARCHAR(200),
    VALUE VARCHAR(200),
    STATUS VARCHAR(50),
    CREATE_TIME TIMESTAMP,
    DESCRIPTION VARCHAR(200),
    USER_ID VARCHAR(64),
    TENANT_ID VARCHAR(50),
    OBJECT_ID BIGINT,
    CONSTRAINT PK_OSS_METADATA PRIMARY KEY(ID),
    CONSTRAINT FK_OSS_METADATA_OBJECT FOREIGN KEY(OBJECT_ID) REFERENCES OSS_OBJECT(ID)
) ENGINE=INNODB CHARSET=UTF8;
