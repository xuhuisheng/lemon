

-------------------------------------------------------------------------------
--  whitelist host
-------------------------------------------------------------------------------
CREATE TABLE WHITELIST_HOST(
        ID BIGINT NOT NULL,
        VALUE VARCHAR(50),
	PRIORITY INT,
	APP_ID BIGINT,
        CONSTRAINT PK_WHITELIST_HOST PRIMARY KEY(ID),
        CONSTRAINT FK_WHITELIST_HOST_APP FOREIGN KEY(APP_ID) REFERENCES WHITELIST_APP(ID)
);

