

-------------------------------------------------------------------------------
--  cms tag
-------------------------------------------------------------------------------
CREATE TABLE CMS_TAG(
        ID BIGINT NOT NULL,
	NAME VARCHAR(50),
	COUNT_ARTICLE INT,
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_CMS_TAG PRIMARY KEY(ID)
);

