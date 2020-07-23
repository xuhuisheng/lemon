

-------------------------------------------------------------------------------
--  sys category
-------------------------------------------------------------------------------
CREATE TABLE SYS_CATEGORY(
    ID BIGINT NOT NULL,
    CODE VARCHAR(50),
    NAME VARCHAR(50),
	LOGO VARCHAR(200),
    PRIORITY INTEGER,
    STATUS VARCHAR(50),
    DESCN VARCHAR(200),
	CREATE_TIME TIMESTAMP,
	USER_ID VARCHAR(64),
    PARENT_ID BIGINT,

    TENANT_ID VARCHAR(64),

    CONSTRAINT PK_SYS_CATEGORY PRIMARY KEY(ID),
    CONSTRAINT FK_SYS_CATEGORY_PARENT FOREIGN KEY(PARENT_ID) REFERENCES SYS_CATEGORY(ID)
) ENGINE=INNODB CHARSET=UTF8;















