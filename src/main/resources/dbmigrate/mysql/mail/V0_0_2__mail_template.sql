

-------------------------------------------------------------------------------
--  mail template
-------------------------------------------------------------------------------
CREATE TABLE MAIL_TEMPLATE(
        ID BIGINT auto_increment,
        NAME VARCHAR(50),
	RECEIVER VARCHAR(200),
	SENDER VARCHAR(200),
	CC VARCHAR(200),
	BCC VARCHAR(200),
	SUBJECT VARCHAR(200),
	CONTENT TEXT,
	MANUAL INT,
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_MAIL_TEMPLATE PRIMARY KEY(ID)
) engine=innodb;

