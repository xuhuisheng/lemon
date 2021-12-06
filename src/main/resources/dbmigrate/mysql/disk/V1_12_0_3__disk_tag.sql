

-------------------------------------------------------------------------------
--  disk tag
-------------------------------------------------------------------------------
CREATE TABLE DISK_TAG(
    ID BIGINT NOT NULL,
    CODE VARCHAR(50),
    NAME VARCHAR(50),
    TENANT_ID VARCHAR(64),
    CONSTRAINT PK_DISK_TAG PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

CREATE INDEX IDX_DISK_TAG_CODE ON DISK_TAG(CODE);







