

-------------------------------------------------------------------------------
--  whitelist ip
-------------------------------------------------------------------------------
CREATE TABLE WHITELIST_IP(
        ID BIGINT NOT NULL,
        VALUE VARCHAR(50),
	PRIORITY INT,
	APP_ID BIGINT,
        CONSTRAINT PK_WHITELIST_IP PRIMARY KEY(ID),
        CONSTRAINT FK_WHITELIST_IP_APP FOREIGN KEY(APP_ID) REFERENCES WHITELIST_APP(ID)
);

