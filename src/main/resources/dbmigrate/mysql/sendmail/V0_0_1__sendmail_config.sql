

-------------------------------------------------------------------------------
--  sendmail config
-------------------------------------------------------------------------------
CREATE TABLE SENDMAIL_CONFIG(
        ID BIGINT NOT NULL,
        NAME VARCHAR(50),
	HOST VARCHAR(200),
	USERNAME VARCHAR(200),
	PASSWORD VARCHAR(200),
	SMTP_AUTH INT,
	SMTP_STARTTLS INT,
	STATUS VARCHAR(50),
	DEFAULT_FROM VARCHAR(200),
	TEST_MAIL VARCHAR(200),
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_SENDMAIL_CONFIG PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;
