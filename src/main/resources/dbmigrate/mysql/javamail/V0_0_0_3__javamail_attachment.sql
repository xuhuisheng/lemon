

-------------------------------------------------------------------------------
--  javamail attachment
-------------------------------------------------------------------------------
CREATE TABLE JAVAMAIL_ATTACHMENT(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	REF VARCHAR(200),
	TYPE VARCHAR(50),
	MESSAGE_ID BIGINT,
        CONSTRAINT PK_JAVAMAIL_ATTACHMENT PRIMARY KEY(ID),
	CONSTRAINT FK_JAVAMAIL_ATTACHMENT_MESSAGE FOREIGN KEY(MESSAGE_ID) REFERENCES JAVAMAIL_MESSAGE(ID)
) ENGINE=INNODB CHARSET=UTF8;

