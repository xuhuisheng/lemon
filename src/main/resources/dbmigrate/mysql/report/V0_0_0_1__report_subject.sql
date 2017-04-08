

-------------------------------------------------------------------------------
--  report subject
-------------------------------------------------------------------------------
CREATE TABLE REPORT_SUBJECT(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	DESCRIPTION VARCHAR(200),
	PRIORITY INT,
        CONSTRAINT PK_REPORT_SUBJECT PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

