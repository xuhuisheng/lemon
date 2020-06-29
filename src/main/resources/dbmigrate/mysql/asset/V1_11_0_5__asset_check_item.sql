

-------------------------------------------------------------------------------
--  asset check item
-------------------------------------------------------------------------------
CREATE TABLE ASSET_CHECK_ITEM(
    ID BIGINT NOT NULL,

    CODE VARCHAR(100),
    PRODUCT_CATEGORY VARCHAR(100),
    PURPOSE VARCHAR(100),
    PRODUCT_NAME VARCHAR(200),
    PRODUCT_MODEL VARCHAR(50),
    PRODUCT_PRICE INT,
    PRODUCT_NUM INT,
    DESCRIPTION VARCHAR(200),
    TENANT_ID VARCHAR(64),
    INFO_ID BIGINT,

    CONSTRAINT PK_ASSET_CHECK_ITEM PRIMARY KEY(ID),
    CONSTRAINT FK_ASSET_CHECK_ITEM_INFO FOREIGN KEY(INFO_ID) REFERENCES ASSET_CHECK_INFO(ID)
) ENGINE=INNODB CHARSET=UTF8;




