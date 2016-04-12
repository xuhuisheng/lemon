

-------------------------------------------------------------------------------
--  vote user
-------------------------------------------------------------------------------
CREATE TABLE VOTE_USER(
        ID BIGINT NOT NULL,
	USER_ID VARCHAR(64),
	CREATE_TIME DATETIME,
	TENANT_ID VARCHAR(64),
	ITEM_ID BIGINT,
        CONSTRAINT PK_VOTE_USER PRIMARY KEY(ID),
        CONSTRAINT FK_VOTE_USER_ITEM FOREIGN KEY(ITEM_ID) REFERENCES VOTE_ITEM(ID)
) ENGINE=INNODB CHARSET=UTF8;

