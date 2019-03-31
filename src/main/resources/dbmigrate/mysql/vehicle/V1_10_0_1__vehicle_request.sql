

-------------------------------------------------------------------------------
--  vehicle request
-------------------------------------------------------------------------------
CREATE TABLE VEHICLE_REQUEST(
    ID BIGINT NOT NULL,
    CODE VARCHAR(100),
    USER_ID VARCHAR(64),
    DEPT_CODE VARCHAR(50),
    DEPT_NAME VARCHAR(200),
    CREATE_TIME DATETIME,
    STATUS VARCHAR(50),
    DESCRIPTION VARCHAR(200),
    TENANT_ID VARCHAR(64),

    VEHICLE_NAME VARCHAR(50),
    START_DATE DATE,
    END_DATE DATE,
    LOCATION VARCHAR(200),
    ENTORAGE VARCHAR(200),

    CONSTRAINT PK_VEHICLE_REQUEST PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;



















