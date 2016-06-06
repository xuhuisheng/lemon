

-------------------------------------------------------------------------------
--  sendsms config
-------------------------------------------------------------------------------
CREATE TABLE SENDSMS_CONFIG(
        ID BIGINT,
        NAME VARCHAR(50),
	HOST VARCHAR(200),
	USERNAME VARCHAR(200),
	PASSWORD VARCHAR(200),
	APP_ID VARCHAR(50),
	DESCRIPTION VARCHAR(200),
	MOBILE_FIELD_NAME VARCHAR(50),
	MESSAGE_FIELD_NAME VARCHAR(50),
	TENANT_ID VARCHAR(50),
        CONSTRAINT PK_SENDSMS_CONFIG PRIMARY KEY(ID)
);
