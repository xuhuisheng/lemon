

-------------------------------------------------------------------------------
--  report info
-------------------------------------------------------------------------------
CREATE TABLE REPORT_INFO(
        ID BIGINT NOT NULL,
	CODE VARCHAR(100),
	NAME VARCHAR(100),
	TYPE VARCHAR(100),
	DESCRIPTION VARCHAR(200),
	CONTENT VARCHAR(200),
	PRIORITY INT,
	QUERY_ID BIGINT,
        CONSTRAINT PK_REPORT_INFO PRIMARY KEY(ID),
	CONSTRAINT FK_REPORT_INFO_QUERY FOREIGN KEY (QUERY_ID) REFERENCES REPORT_QUERY(ID)
) ENGINE=INNODB CHARSET=UTF8;

