

-------------------------------------------------------------------------------
--  model category
-------------------------------------------------------------------------------
CREATE TABLE MODEL_CATEGORY(
        ID BIGINT AUTO_INCREMENT,
	CODE VARCHAR(50),
	NAME VARCHAR(200),
	DESCRIPTION VARCHAR(200),
	TENANT_ID VARCHAR(64),
	CONSTRAINT PK_MODEL_CAETGOR PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

