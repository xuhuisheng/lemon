

-------------------------------------------------------------------------------
--  mail attachment
-------------------------------------------------------------------------------
CREATE TABLE MAIL_ATTACHMENT(
        ID BIGINT auto_increment,
        NAME VARCHAR(50),
	PATH VARCHAR(200),
	SCOPE_ID VARCHAR(50),
	TEMPLATE_ID BIGINT,
        CONSTRAINT PK_MAIL_ATTACHMENT PRIMARY KEY(ID),
	CONSTRAINT FK_MAIL_ATTACHMENT_TEMPLATE FOREIGN KEY(TEMPLATE_ID) REFERENCES MAIL_TEMPLATE(ID)
) engine=innodb;

