

-------------------------------------------------------------------------------
--  mail config
-------------------------------------------------------------------------------
CREATE TABLE MAIL_CONFIG(
        ID BIGINT auto_increment,
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
        CONSTRAINT PK_MAIL_CONFIG PRIMARY KEY(ID)
) engine=innodb;
