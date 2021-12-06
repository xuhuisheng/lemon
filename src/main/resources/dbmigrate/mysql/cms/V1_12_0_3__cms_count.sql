

-------------------------------------------------------------------------------
--  cms count
-------------------------------------------------------------------------------
CREATE TABLE CMS_COUNT(
    ID BIGINT AUTO_INCREMENT,
    CODE VARCHAR(50),
    VALUE INT,
    ARTICLE_ID BIGINT,
    CONSTRAINT PK_CMS_COUNT PRIMARY KEY(ID),
    CONSTRAINT FK_CMS_COUNT_ARTICLE FOREIGN KEY(ARTICLE_ID) REFERENCES CMS_ARTICLE(ID)
) ENGINE=INNODB CHARSET=UTF8;
