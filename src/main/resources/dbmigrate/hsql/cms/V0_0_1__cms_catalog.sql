

-------------------------------------------------------------------------------
--  cms catalog
-------------------------------------------------------------------------------
CREATE TABLE CMS_CATALOG(
        ID BIGINT NOT NULL,
	NAME VARCHAR(50),
	CODE VARCHAR(200),
	LOGO VARCHAR(200),
	TYPE INT,
	TEMPLATE_INDEX VARCHAR(200),
	TEMPLATE_LIST VARCHAR(200),
	TEMPLATE_DETAIL VARCHAR(200),
	KEYWORD VARCHAR(200),
	DESCRIPTION VARCHAR(200),
	PARENT_ID BIGINT,
        CONSTRAINT PK_CMS_CATALOG PRIMARY KEY(ID),
	CONSTRAINT FK_CMS_CATALOG_PARENT FOREIGN KEY(PARENT_ID) REFERENCES CMS_CATALOG(ID)
);

