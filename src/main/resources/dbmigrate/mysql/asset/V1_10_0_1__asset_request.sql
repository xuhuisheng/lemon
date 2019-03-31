

-------------------------------------------------------------------------------
--  asset request
-------------------------------------------------------------------------------
CREATE TABLE ASSET_REQUEST(
    ID BIGINT NOT NULL,
    CODE VARCHAR(100),
    USER_ID VARCHAR(64),
    DEPT_CODE VARCHAR(50),
    DEPT_NAME VARCHAR(200),
    CREATE_TIME DATETIME,
    STATUS VARCHAR(50),
    DESCRIPTION VARCHAR(200),
    TENANT_ID VARCHAR(64),

    PRODUCT_CATEGORY VARCHAR(100),
    PURPOSE VARCHAR(100),
    PRODUCT_NAME VARCHAR(200),
    PRODUCT_MODEL VARCHAR(50),
    PRODUCT_PRICE INT,
    PRODUCT_NUM INT,

    CONSTRAINT PK_ASSET_REQUEST PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;



















