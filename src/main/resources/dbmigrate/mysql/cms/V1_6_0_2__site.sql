

-------------------------------------------------------------------------------
--  cms site
-------------------------------------------------------------------------------
CREATE TABLE CMS_SITE(
        ID BIGINT NOT NULL,
	TYPE VARCHAR(50),
	NAME VARCHAR(50),
	CODE VARCHAR(200),
	PRIORITY INT,
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_CMS_SITE PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

