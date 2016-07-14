

-------------------------------------------------------------------------------
--  vote item
-------------------------------------------------------------------------------
CREATE TABLE VOTE_ITEM(
        ID BIGINT NOT NULL,
	NAME VARCHAR(100),
	STYLE VARCHAR(50),
	PRIORITY INTEGER,
	TENANT_ID VARCHAR(64),
	HEAD_COUNT INTEGER,
	INFO_ID BIGINT,
        CONSTRAINT PK_VOTE_ITEM PRIMARY KEY(ID),
        CONSTRAINT FK_VOTE_ITEM_INFO FOREIGN KEY(INFO_ID) REFERENCES VOTE_INFO(ID)
) ENGINE=INNODB CHARSET=UTF8;

