

-------------------------------------------------------------------------------
--  pim phrase
-------------------------------------------------------------------------------
CREATE TABLE PIM_PHRASE(
        ID BIGINT NOT NULL,
	CONTENT VARCHAR(200),
	USER_ID VARCHAR(64),
        CONSTRAINT PK_PIM_PHRASE PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;






