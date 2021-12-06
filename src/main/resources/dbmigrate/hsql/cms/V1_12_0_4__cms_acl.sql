

-------------------------------------------------------------------------------
--  cms acl
-------------------------------------------------------------------------------
CREATE TABLE CMS_ACL(
    ID BIGINT NOT NULL,
    MASK INT,
    TYPE VARCHAR(50),
    REF VARCHAR(50),
    CREATE_TIME DATETIME,
    TENANT_ID VARCHAR(64),
    CATALOG_ID BIGINT,
    ARTICLE_ID BIGINT,
    CONSTRAINT PK_CMS_ACL PRIMARY KEY(ID),
    CONSTRAINT FK_CMS_ACL_CATALOG FOREIGN KEY(CATALOG_ID) REFERENCES CMS_CATALOG(ID),
    CONSTRAINT FK_CMS_ACL_ARTICLE FOREIGN KEY(ARTICLE_ID) REFERENCES CMS_ARTICLE(ID)
);

