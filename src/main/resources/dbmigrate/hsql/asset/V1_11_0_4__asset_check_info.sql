

-------------------------------------------------------------------------------
--  asset check info
-------------------------------------------------------------------------------
CREATE TABLE ASSET_CHECK_INFO(
    ID BIGINT NOT NULL,
    CODE VARCHAR(100),
    NAME VARCHAR(200),
    USER_ID VARCHAR(64),
    DEPT_CODE VARCHAR(50),
    DEPT_NAME VARCHAR(200),
    CREATE_TIME DATETIME,
    STATUS VARCHAR(50),
    DESCRIPTION VARCHAR(200),
    TENANT_ID VARCHAR(64),

    CONSTRAINT PK_ASSET_CHECK_INFO PRIMARY KEY(ID)
);

COMMENT ON TABLE ASSET_CHECK_INFO IS '资产盘点';

