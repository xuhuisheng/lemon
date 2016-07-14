

-------------------------------------------------------------------------------
--  model info
-------------------------------------------------------------------------------
CREATE TABLE MODEL_INFO(
        ID BIGINT AUTO_INCREMENT,
	CODE VARCHAR(200),
	NAME VARCHAR(200),
        TYPE VARCHAR(50),
	TENANT_ID VARCHAR(64),
	CONSTRAINT PK_MODEL_INFO PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

