

-------------------------------------------------------------------------------
--  open app
-------------------------------------------------------------------------------
CREATE TABLE OPEN_APP(
    ID BIGINT NOT NULL,
    CODE VARCHAR(50),
    NAME VARCHAR(50),
    CLIENT_ID VARCHAR(100),
    CLIENT_SECRET VARCHAR(100),
    STATUS VARCHAR(50),
    USER_ID VARCHAR(64),
    DESCRIPTION VARCHAR(200),
    CREATE_TIME DATETIME,
    GROUP_CODE VARCHAR(50),
    TENANT_ID VARCHAR(50),
    CONSTRAINT PK_OPEN_APP PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;














