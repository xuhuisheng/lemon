

-------------------------------------------------------------------------------
--  report query
-------------------------------------------------------------------------------
CREATE TABLE REPORT_QUERY(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	DESCRIPTION VARCHAR(200),
	CONTENT VARCHAR(200),
	PRIORITY INT,
	SUBJECT_ID BIGINT,
        CONSTRAINT PK_REPORT_QUERY PRIMARY KEY(ID),
	CONSTRAINT FK_REPORT_QUERY_SUBJECT FOREIGN KEY (SUBJECT_ID) REFERENCES REPORT_SUBJECT(ID)
) ENGINE=INNODB CHARSET=UTF8;

