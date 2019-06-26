

-------------------------------------------------------------------------------
--  comment info
-------------------------------------------------------------------------------
CREATE TABLE COMMENT_INFO(
    ID BIGINT NOT NULL,
	PARENT_ID BIGINT,
	CONTENT VARCHAR(200),
    CREATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP ,
    LIKES INT,
    DISLIKES INT,
    USER_ID VARCHAR(64),
    USER_NAME VARCHAR(50),
    USER_AVATAR VARCHAR(200),
    THREAD_ID BIGINT,
    MODE VARCHAR(10),
    IP VARCHAR(50),
    URL VARCHAR(200),
    CONVERSATION VARCHAR(200),
	TENANT_ID VARCHAR(64),
    CONSTRAINT PK_COMMENT_INFO PRIMARY KEY(ID),
    CONSTRAINT FK_COMMENT_INFO_THREAD FOREIGN KEY(THREAD_ID) REFERENCES COMMENT_THREAD(ID),
    CONSTRAINT FK_COMMENT_INFO_PARENT FOREIGN KEY(PARENT_ID) REFERENCES COMMENT_INFO(ID)
) ENGINE=INNODB CHARSET=UTF8;

