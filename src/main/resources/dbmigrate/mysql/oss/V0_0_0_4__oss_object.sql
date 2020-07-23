

-------------------------------------------------------------------------------
--  oss object
-------------------------------------------------------------------------------
CREATE TABLE OSS_OBJECT(
    ID BIGINT NOT NULL,
    CODE VARCHAR(50),
    NAME VARCHAR(200),
    PATH VARCHAR(200),
    STATUS VARCHAR(50),
    CREATE_TIME TIMESTAMP,
    DESCRIPTION VARCHAR(200),
    USER_ID VARCHAR(64),
    TENANT_ID VARCHAR(50),
    BUCKET_ID BIGINT,
    CONSTRAINT PK_OSS_OBJECT PRIMARY KEY(ID),
    CONSTRAINT FK_OSS_OBJECT_BUCKET FOREIGN KEY(BUCKET_ID) REFERENCES OSS_BUCKET(ID)
) ENGINE=INNODB CHARSET=UTF8;
