

-------------------------------------------------------------------------------
--  cms template content
-------------------------------------------------------------------------------
CREATE TABLE CMS_TEMPLATE_CONTENT(
    ID BIGINT NOT NULL,
	TYPE VARCHAR(50),
	NAME VARCHAR(50),
	PRIORITY INT,
	CATALOG_ID BIGINT,
	CONTENT TEXT,
	PATH VARCHAR(200),
	TENANT_ID VARCHAR(64),
    CONSTRAINT PK_CMS_TEMPLATE_CONTENT PRIMARY KEY(ID),
    CONSTRAINT FK_CMS_TEMPLATE_CONTENT_CATALOG FOREIGN KEY(CATALOG_ID) REFERENCES CMS_TEMPLATE_CATALOG(ID)
) ENGINE=INNODB CHARSET=UTF8;

