

-------------------------------------------------------------------------------
--  notification log
-------------------------------------------------------------------------------
CREATE TABLE NOTIFICATION_LOG(
    ID BIGINT AUTO_INCREMENT,
    CODE VARCHAR(50),
    NAME VARCHAR(50),
    CONTENT TEXT,
    CREATE_TIME TIMESTAMP,
    CREATOR VARCHAR(64),
    STATUS VARCHAR(50),
    APP VARCHAR(50),

    CATALOG VARCHAR(50),
    CONSTRAINT PK_NOTIFICATION_LOG PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;












