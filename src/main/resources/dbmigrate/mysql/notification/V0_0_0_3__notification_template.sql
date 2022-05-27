

-------------------------------------------------------------------------------
--  notification template
-------------------------------------------------------------------------------
CREATE TABLE NOTIFICATION_TEMPLATE(
    ID BIGINT AUTO_INCREMENT,
    CODE VARCHAR(50),
    NAME VARCHAR(50),
    CONTENT TEXT,
    EXTRA TEXT,

    PRIORITY INT,
    CREATE_TIME TIMESTAMP,
    CREATOR VARCHAR(64),
    UPDATE_TIME TIMESTAMP,
    UPDATER VARCHAR(64),
    STATUS VARCHAR(50),

    APP VARCHAR(50),

    CONSTRAINT PK_NOTIFICATION_TEMPLATE PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;















