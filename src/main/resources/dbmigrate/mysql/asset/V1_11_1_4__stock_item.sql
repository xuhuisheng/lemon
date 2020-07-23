

-------------------------------------------------------------------------------
--  stock item
-------------------------------------------------------------------------------
CREATE TABLE STOCK_ITEM(
    ID BIGINT NOT NULL,

    CODE VARCHAR(50),
    SN VARCHAR(50),
    USING_COUNT INT,
    USER_ID VARCHAR(64),
    STATUS VARCHAR(50),

    DESCRIPTION VARCHAR(200),
    TENANT_ID VARCHAR(64),
    INFO_ID BIGINT,
    ITEM_ID BIGINT,

    CONSTRAINT PK_STOCK_ITEM PRIMARY KEY(ID),
    CONSTRAINT FK_STOCK_ITEM_INFO FOREIGN KEY(INFO_ID) REFERENCES STOCK_INFO(ID),
    CONSTRAINT FK_STOCK_ITEM_ITEM FOREIGN KEY(ITEM_ID) REFERENCES STOCK_ITEM(ID)
) ENGINE=INNODB CHARSET=UTF8;




