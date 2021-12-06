

-------------------------------------------------------------------------------
--  cms attr
-------------------------------------------------------------------------------
CREATE TABLE CMS_ATTR(
    ID BIGINT NOT NULL,
    CODE VARCHAR(50),
    NAME VARCHAR(50),
    VALUE VARCHAR(50),
    PRIORITY INT,
    CATALOG VARCHAR(50),
    ROW_INDEX INT,
    TYPE VARCHAR(50),
    CREATE_TIME DATETIME,
    TENANT_ID VARCHAR(64),
    ARTICLE_ID BIGINT,
    CONSTRAINT PK_CMS_ATTR PRIMARY KEY(ID),
    CONSTRAINT FK_CMS_ATTR_ARTICLE FOREIGN KEY(ARTICLE_ID) REFERENCES CMS_ARTICLE(ID)
) ENGINE=INNODB CHARSET=UTF8;

