

-------------------------------------------------------------------------------
--  javamail config
-------------------------------------------------------------------------------
CREATE TABLE JAVAMAIL_CONFIG(
        ID BIGINT NOT NULL,
	USERNAME VARCHAR(200),
	PASSWORD VARCHAR(200),
	RECEIVE_TYPE VARCHAR(50),
	RECEIVE_HOST VARCHAR(200),
	RECEIVE_PORT VARCHAR(10),
	RECEIVE_SECURE VARCHAR(50),
	SEND_TYPE VARCHAR(50),
	SEND_HOST VARCHAR(50),
	SEND_PORT VARCHAR(10),
	SEND_SECURE VARCHAR(50),
	PRIORITY INT,
	USER_ID VARCHAR(64),
        CONSTRAINT PK_JAVAMAIL_CONFIG PRIMARY KEY(ID)
);

