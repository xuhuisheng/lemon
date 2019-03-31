

-------------------------------------------------------------------------------
--  leave rule
-------------------------------------------------------------------------------
CREATE TABLE LEAVE_RULE(
    ID BIGINT NOT NULL,
    CODE VARCHAR(50),
    NAME VARCHAR(200),
    TYPE VARCHAR(50),
    CREATE_TIME DATETIME,
    STATUS VARCHAR(50),
    DESCRIPTION VARCHAR(200),
    TENANT_ID VARCHAR(64),
    CONSTRAINT PK_LEAVE_RULE PRIMARY KEY(ID)
);

