

-------------------------------------------------------------------------------
--  disk tag info
-------------------------------------------------------------------------------
CREATE TABLE DISK_TAG_INFO(
    ID BIGINT NOT NULL,
    TYPE VARCHAR(50),
    TAG_ID BIGINT,
    INFO_ID BIGINT,
    PRIORITY INT,
    TENANT_ID VARCHAR(64),
    CONSTRAINT PK_DISK_TAG_INFO PRIMARY KEY(ID),
    CONSTRAINT FK_DISK_TAG_INFO_TAG FOREIGN KEY(TAG_ID) REFERENCES DISK_TAG(ID),
    CONSTRAINT FK_DISK_TAG_INFO_INFO FOREIGN KEY(INFO_ID) REFERENCES DISK_INFO(ID)
) ENGINE=INNODB CHARSET=UTF8;









