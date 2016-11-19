

-------------------------------------------------------------------------------
--  cms tag article
-------------------------------------------------------------------------------
CREATE TABLE CMS_TAG_ARTICLE(
        ID BIGINT NOT NULL,
	TYPE VARCHAR(50),
	TAG_ID BIGINT,
	ARTICLE_ID BIGINT,
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_CMS_TAG_ARTICLE PRIMARY KEY(ID),
	CONSTRAINT FK_CMS_TAG_ARTICLE_TAG FOREIGN KEY(TAG_ID) REFERENCES CMS_TAG(ID),
	CONSTRAINT FK_CMS_TAG_ARTICLE_ARTICLE FOREIGN KEY(ARTICLE_ID) REFERENCES CMS_ARTICLE(ID)
);

