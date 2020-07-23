

-------------------------------------------------------------------------------
--  card info
-------------------------------------------------------------------------------
CREATE TABLE CARD_INFO(
    ID BIGINT NOT NULL,
    TYPE VARCHAR(50),
    CODE VARCHAR(200),
    USER_ID VARCHAR(64),
    CREATE_TIME DATETIME,
    OPERATE_USER VARCHAR(64),
    OPERATE_TIME DATETIME,
    OPERATE_LOCATION VARCHAR(200),
    STATUS VARCHAR(50),
    DESCRIPTION VARCHAR(200),
    TENANT_ID VARCHAR(64),
    CONSTRAINT PK_CARD_INFO PRIMARY KEY(ID)
);

