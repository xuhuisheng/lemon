

-------------------------------------------------------------------------------
-- account avatar
-------------------------------------------------------------------------------
CREATE TABLE ACCOUNT_AVATAR(
        ID BIGINT NOT NULL,
	TYPE VARCHAR(50),
	CODE VARCHAR(200),
	ACCOUNT_ID BIGINT,
        CONSTRAINT PK_ACCOUNT_AVATAR PRIMARY KEY(ID),
        CONSTRAINT FK_ACCOUNT_AVATAR_ACCOUNT FOREIGN KEY(ACCOUNT_ID) REFERENCES ACCOUNT_INFO(ID)
) ENGINE=INNODB CHARSET=UTF8;







