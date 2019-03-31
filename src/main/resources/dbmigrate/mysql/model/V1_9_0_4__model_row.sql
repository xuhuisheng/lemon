

-------------------------------------------------------------------------------
--  model row
-------------------------------------------------------------------------------
CREATE TABLE MODEL_ROW(
        ID BIGINT AUTO_INCREMENT,
	PRIORITY VARCHAR(50),
	BASE_ID BIGINT,
	TENANT_ID VARCHAR(64),
	CONSTRAINT PK_MODEL_ROW PRIMARY KEY(ID),
        CONSTRAINT FK_MODEL_ROW_BASE FOREIGN KEY(BASE_ID) REFERENCES MODEL_BASE(ID)
) ENGINE=INNODB CHARSET=UTF8;

