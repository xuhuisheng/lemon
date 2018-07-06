

-------------------------------------------------------------------------------
--  model schema
-------------------------------------------------------------------------------
CREATE TABLE MODEL_SCHEMA(
        ID BIGINT AUTO_INCREMENT,
	CATALOG VARCHAR(50),
	CODE VARCHAR(50),
	LABEL VARCHAR(200),
	TYPE VARCHAR(50),
	PRIORITY INT,
	CATEGORY_ID BIGINT,
	TENANT_ID VARCHAR(64),
	CONSTRAINT PK_MODEL_SCHEMA PRIMARY KEY(ID),
        CONSTRAINT FK_MODEL_SCHEMA_CATEGORY FOREIGN KEY(CATEGORY_ID) REFERENCES MODEL_CATEGORY(ID)
) ENGINE=INNODB CHARSET=UTF8;

