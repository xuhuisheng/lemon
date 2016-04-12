

-------------------------------------------------------------------------------
--  cms content
-------------------------------------------------------------------------------
CREATE TABLE CMS_CONTENT(
        ID BIGINT NOT NULL,
	NAME VARCHAR(50),
	CODE VARCHAR(200),
	CONTENT VARCHAR(65535),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_CMS_CONTENT PRIMARY KEY(ID)
);

