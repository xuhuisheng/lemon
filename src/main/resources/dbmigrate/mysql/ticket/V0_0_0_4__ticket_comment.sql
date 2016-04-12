

-------------------------------------------------------------------------------
--  ticket comment
-------------------------------------------------------------------------------
CREATE TABLE TICKET_COMMENT(
        ID BIGINT NOT NULL,
	CONTENT TEXT,
	CREATOR VARCHAR(64),
	STATUS VARCHAR(50),
	CREATE_TIME DATETIME,
	INFO_ID BIGINT,
	CONSTRAINT PK_TICKET_COMMENT PRIMARY KEY(ID),
	CONSTRAINT FK_TICKET_COMMENT_INFO FOREIGN KEY (INFO_ID) REFERENCES TICKET_INFO(ID)
) ENGINE=INNODB CHARSET=UTF8;

