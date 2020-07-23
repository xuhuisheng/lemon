

-------------------------------------------------------------------------------
--  travel item
-------------------------------------------------------------------------------
CREATE TABLE TRAVEL_ITEM(
    ID BIGINT NOT NULL,
    INTERNATIONAL VARCHAR(50),
    VEHICLE VARCHAR(100),
    TYPE VARCHAR(100),
    START_CITY VARCHAR(50),
    END_CITY VARCHAR(50),
    START_DATE DATE,
    END_DATE DATE,
    DAY INT,
    PRIORITY INT,
    INFO_ID BIGINT,
    CONSTRAINT PK_TRAVEL_ITEM PRIMARY KEY(ID),
    CONSTRAINT FK_TRAVEL_ITEM_INFO FOREIGN KEY(INFO_ID) REFERENCES TRAVEL_INFO(ID)
) ENGINE=INNODB CHARSET=UTF8;













