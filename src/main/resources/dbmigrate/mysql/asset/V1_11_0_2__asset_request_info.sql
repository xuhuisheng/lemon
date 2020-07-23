

-------------------------------------------------------------------------------
--  asset request info
-------------------------------------------------------------------------------
CREATE TABLE ASSET_REQUEST_INFO(
    ID BIGINT NOT NULL,
    CODE VARCHAR(100),
    USER_ID VARCHAR(64),
    DEPT_CODE VARCHAR(50),
    DEPT_NAME VARCHAR(200),
    CREATE_TIME DATETIME,
    STATUS VARCHAR(50),
    DESCRIPTION VARCHAR(200),
    TENANT_ID VARCHAR(64),

    PURPOSE VARCHAR(100),

    CONSTRAINT PK_ASSET_REQUEST_INFO PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;














