

-------------------------------------------------------------------------------
--  sendmail attachment
-------------------------------------------------------------------------------
CREATE TABLE SENDMAIL_ATTACHMENT(
        ID BIGINT NOT NULL,
        NAME VARCHAR(50),
	PATH VARCHAR(200),
	SCOPE_ID VARCHAR(50),
	TEMPLATE_ID BIGINT,
        CONSTRAINT PK_SENDMAIL_ATTACHMENT PRIMARY KEY(ID),
	CONSTRAINT FK_SENDMAIL_ATTACHMENT_TEMPLATE FOREIGN KEY(TEMPLATE_ID) REFERENCES SENDMAIL_TEMPLATE(ID)
) ENGINE=INNODB CHARSET=UTF8;

