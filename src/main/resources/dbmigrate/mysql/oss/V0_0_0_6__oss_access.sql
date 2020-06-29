

-------------------------------------------------------------------------------
--  oss access
-------------------------------------------------------------------------------
CREATE TABLE OSS_ACCESS(
    ID BIGINT NOT NULL,
    TYPE VARCHAR(50),
    ACCESS_KEY VARCHAR(100),
    SECRET_KEY VARCHAR(100),
    STATUS VARCHAR(50),
    CREATE_TIME TIMESTAMP,
    DESCRIPTION VARCHAR(200),
    USER_ID VARCHAR(64),
    TENANT_ID VARCHAR(50),
    BUCKET_ID BIGINT,
    CONSTRAINT PK_OSS_ACCESS PRIMARY KEY(ID),
    CONSTRAINT FK_OSS_ACCESS_BUCKET FOREIGN KEY(BUCKET_ID) REFERENCES OSS_BUCKET(ID)
) ENGINE=INNODB CHARSET=UTF8;
