

-------------------------------------------------------------------------------
--  travel request
-------------------------------------------------------------------------------
CREATE TABLE TRAVEL_REQUEST(
    ID BIGINT NOT NULL,
    CODE VARCHAR(100),
    USER_ID VARCHAR(64),
    DEPT_CODE VARCHAR(50),
    DEPT_NAME VARCHAR(200),
    CREATE_TIME DATETIME,
    STATUS VARCHAR(50),
    DESCRIPTION VARCHAR(200),
    TENANT_ID VARCHAR(64),

    VEHICLE VARCHAR(100),
    TYPE VARCHAR(100),
    START_CITY VARCHAR(50),
    END_CITY VARCHAR(50),
    START_DATE DATE,
    END_DATE DATE,
    DAY INT,
    PEER VARCHAR(200),

    CONSTRAINT PK_TRAVEL_REQUEST PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;






















