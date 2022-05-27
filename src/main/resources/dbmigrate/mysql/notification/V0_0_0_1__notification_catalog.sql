

-------------------------------------------------------------------------------
--  notification catalog
-------------------------------------------------------------------------------
CREATE TABLE NOTIFICATION_CATALOG(
    ID BIGINT AUTO_INCREMENT,
    CODE VARCHAR(50),
    NAME VARCHAR(200),

    PRIORITY INT,
    CREATE_TIME TIMESTAMP,
    CREATOR VARCHAR(64),
    UPDATE_TIME TIMESTAMP,
    UPDATER VARCHAR(64),
    STATUS VARCHAR(50),

    CONSTRAINT PK_NOTIFICATION_CATALOG PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;












