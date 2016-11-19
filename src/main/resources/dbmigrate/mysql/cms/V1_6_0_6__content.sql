

-------------------------------------------------------------------------------
--  cms content
-------------------------------------------------------------------------------
CREATE TABLE CMS_CONTENT(
        ID BIGINT NOT NULL,
	NAME VARCHAR(50),
	CODE VARCHAR(200),
	CONTENT TEXT,
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_CMS_CONTENT PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

