

-------------------------------------------------------------------------------
--  sendmail template
-------------------------------------------------------------------------------
CREATE TABLE SENDMAIL_TEMPLATE(
        ID BIGINT NOT NULL,
        NAME VARCHAR(50),
	RECEIVER VARCHAR(200),
	SENDER VARCHAR(200),
	CC VARCHAR(200),
	BCC VARCHAR(200),
	SUBJECT VARCHAR(200),
	CONTENT VARCHAR(65535),
	MANUAL INT,
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_SENDMAIL_TEMPLATE PRIMARY KEY(ID)
);

