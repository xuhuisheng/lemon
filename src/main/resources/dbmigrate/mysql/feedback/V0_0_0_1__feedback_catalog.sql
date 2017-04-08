

-------------------------------------------------------------------------------
--  feedback catalog
-------------------------------------------------------------------------------
CREATE TABLE FEEDBACK_CATALOG(
        ID BIGINT NOT NULL,
	NAME VARCHAR(100),
	DESCRIPTION VARCHAR(200),
	PRIORITY INT,
	CONSTRAINT PK_FEEDBACK_CATALOG PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

