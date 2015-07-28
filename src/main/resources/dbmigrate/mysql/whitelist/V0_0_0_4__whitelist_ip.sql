

-------------------------------------------------------------------------------
--  whitelist ip
-------------------------------------------------------------------------------
CREATE TABLE WHITELIST_IP(
        ID BIGINT AUTO_INCREMENT,
        VALUE VARCHAR(50),
	PRIORITY INT,
	APP_ID BIGINT,
        CONSTRAINT PK_WHITELIST_IP PRIMARY KEY(ID),
        CONSTRAINT FK_WHITELIST_IP_APP FOREIGN KEY(APP_ID) REFERENCES WHITELIST_APP(ID)
) ENGINE=INNODB CHARSET=UTF8;

