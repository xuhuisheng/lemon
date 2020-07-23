

-------------------------------------------------------------------------------
--  travel info
-------------------------------------------------------------------------------
CREATE TABLE TRAVEL_INFO(
    ID BIGINT NOT NULL,
    CODE VARCHAR(100),
    TYPE VARCHAR(200),
    USER_ID VARCHAR(64),
    DEPT_CODE VARCHAR(50),
    DEPT_NAME VARCHAR(50),
    CREATE_TIME DATETIME,
    STATUS VARCHAR(50),
    PROJECT VARCHAR(50),
    DESCRIPTION VARCHAR(200),
    TENANT_ID VARCHAR(64),

    TRAVELLER VARCHAR(64),
    COST_CENTER_COMPANY VARCHAR(50),
    COST_CENTER_DEPARTMENT VARCHAR(50),
    START_DATE DATETIME,
    END_DATE DATETIME,
    DAY INT,
    PEER VARCHAR(200),
    COST INT,
    CONSTRAINT PK_TRAVEL_INFO PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;























