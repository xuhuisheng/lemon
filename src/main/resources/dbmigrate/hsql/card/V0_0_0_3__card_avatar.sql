

-------------------------------------------------------------------------------
--  card avatar
-------------------------------------------------------------------------------
CREATE TABLE CARD_AVATAR(
    ID BIGINT NOT NULL,
    PATH VARCHAR(200),
    USER_ID VARCHAR(64),
    CREATE_TIME DATETIME,
    STATUS VARCHAR(50),
    DESCRIPTION VARCHAR(200),
    TENANT_ID VARCHAR(64),
    CONSTRAINT PK_CARD_AVATAR PRIMARY KEY(ID)
);

