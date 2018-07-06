

-------------------------------------------------------------------------------
--  ticket attachment
-------------------------------------------------------------------------------
CREATE TABLE TICKET_ATTACHMENT(
        ID BIGINT NOT NULL,
	CODE VARCHAR(200),
	NAME VARCHAR(200),
	FILE_SIZE BIGINT,
	TYPE VARCHAR(50),
	USER_ID VARCHAR(64),
	CREATE_TIME DATETIME,
	INFO_ID BIGINT,
        CONSTRAINT PK_TICKET_ATTACHMENT PRIMARY KEY(ID),
	CONSTRAINT FK_TICKET_ATTACHMENT_INFO FOREIGN KEY (INFO_ID) REFERENCES TICKET_INFO(ID)
) ENGINE=INNODB CHARSET=UTF8;

