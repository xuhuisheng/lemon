

-------------------------------------------------------------------------------
--  sku info
-------------------------------------------------------------------------------
CREATE TABLE SKU_INFO(
    ID BIGINT NOT NULL,

    CODE VARCHAR(100),
    NAME VARCHAR(100),
    STYLE VARCHAR(50),
    MODEL VARCHAR(50),
    COLOR VARCHAR(50),
    SIZE VARCHAR(50),

    PRODUCT_CATEGORY VARCHAR(100),
    PURPOSE VARCHAR(100),
    PRODUCT_NAME VARCHAR(200),
    PRODUCT_MODEL VARCHAR(50),
    PRODUCT_PRICE INT,
    PRODUCT_NUM INT,
    DESCRIPTION VARCHAR(200),
    TENANT_ID VARCHAR(64),
    CATEGORY_ID BIGINT,

    CONSTRAINT PK_SKU_INFO PRIMARY KEY(ID),
    CONSTRAINT FK_SKU_INFO_CATEGORY FOREIGN KEY(CATEGORY_ID) REFERENCES SKU_CATEGORY(ID)
) ENGINE=INNODB CHARSET=UTF8;













