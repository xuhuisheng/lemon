

-------------------------------------------------------------------------------
--  cms template catalog
-------------------------------------------------------------------------------
CREATE TABLE CMS_TEMPLATE_CATALOG(
    ID BIGINT NOT NULL,
	TYPE VARCHAR(50),
	NAME VARCHAR(50),
	PRIORITY INT,
	PATH VARCHAR(200),
	PARENT_ID BIGINT,
	TENANT_ID VARCHAR(64),
    CONSTRAINT PK_CMS_TEMPLATE_CATALOG PRIMARY KEY(ID),
    CONSTRAINT FK_CMS_TEMPLATE_CATALOG_PARENT FOREIGN KEY(PARENT_ID) REFERENCES CMS_TEMPLATE_CATALOG(ID)
) ENGINE=INNODB CHARSET=UTF8;

