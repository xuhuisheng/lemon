

-------------------------------------------------------------------------------
--  comment voter
-------------------------------------------------------------------------------
CREATE TABLE COMMENT_VOTER(
    ID BIGINT NOT NULL,
	TYPE VARCHAR(10),
    CREATE_TIME TIMESTAMP,
    USER_ID VARCHAR(64),
    IP VARCHAR(50),
    INFO_ID BIGINT,
	TENANT_ID VARCHAR(64),
    CONSTRAINT PK_COMMENT_VOTER PRIMARY KEY(ID),
    CONSTRAINT FK_COMMENT_VOTER_INFO FOREIGN KEY(INFO_ID) REFERENCES COMMENT_INFO(ID)
) ENGINE=INNODB CHARSET=UTF8;

