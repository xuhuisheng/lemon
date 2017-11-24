

-------------------------------------------------------------------------------
--  report dim
-------------------------------------------------------------------------------
CREATE TABLE REPORT_DIM(
        ID BIGINT NOT NULL,
	CODE VARCHAR(100),
	NAME VARCHAR(100),
	TYPE VARCHAR(50),
	DESCRIPTION VARCHAR(200),
	PRIORITY INT,
	QUERY_ID BIGINT,
        CONSTRAINT PK_REPORT_DIM PRIMARY KEY(ID),
	CONSTRAINT FK_REPORT_DIM_QUERY FOREIGN KEY (QUERY_ID) REFERENCES REPORT_QUERY(ID)
);

