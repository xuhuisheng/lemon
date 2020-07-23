

-------------------------------------------------------------------------------
--  stock info
-------------------------------------------------------------------------------
CREATE TABLE STOCK_INFO(
    ID BIGINT NOT NULL,

    STORE_COUNT INT,
    USING_COUNT INT,

    DESCRIPTION VARCHAR(200),
    TENANT_ID VARCHAR(64),
    SKU_ID BIGINT,

    CONSTRAINT PK_STOCK_INFO PRIMARY KEY(ID),
    CONSTRAINT FK_STOCK_INFO_SKU FOREIGN KEY(SKU_ID) REFERENCES SKU_INFO(ID)
) ENGINE=INNODB CHARSET=UTF8;




