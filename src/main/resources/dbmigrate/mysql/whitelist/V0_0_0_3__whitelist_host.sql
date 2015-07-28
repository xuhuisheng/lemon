

-------------------------------------------------------------------------------
--  whitelist host
-------------------------------------------------------------------------------
CREATE TABLE WHITELIST_HOST(
        ID BIGINT AUTO_INCREMENT,
        VALUE VARCHAR(50),
	PRIORITY INT,
	APP_ID BIGINT,
        CONSTRAINT PK_WHITELIST_HOST PRIMARY KEY(ID),
        CONSTRAINT FK_WHITELIST_HOST_APP FOREIGN KEY(APP_ID) REFERENCES WHITELIST_APP(ID)
) ENGINE=INNODB CHARSET=UTF8;

