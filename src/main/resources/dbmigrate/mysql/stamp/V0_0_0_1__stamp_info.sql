

-------------------------------------------------------------------------------
--  stamp info
-------------------------------------------------------------------------------
CREATE TABLE STAMP_INFO(
    ID BIGINT NOT NULL,
    COMPANY_NAME VARCHAR(100),
    NAME VARCHAR(200),
    TYPE VARCHAR(50),
    USER_ID VARCHAR(64),
    STATUS VARCHAR(50),
    DESCRIPTION VARCHAR(200),
    TENANT_ID VARCHAR(64),
    CONSTRAINT PK_STAMP_INFO PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;











