

-------------------------------------------------------------------------------
--  model field
-------------------------------------------------------------------------------
CREATE TABLE MODEL_FIELD(
        ID BIGINT AUTO_INCREMENT,
	CODE VARCHAR(200),
	NAME VARCHAR(200),
	TYPE VARCHAR(200),
	PRIORITY INT,
	SEARCHABLE VARCHAR(10),
	DISPLAYABLE VARCHAR(10),
	VIEW_LIST VARCHAR(10),
	VIEW_FORM VARCHAR(10),
	TENANT_ID VARCHAR(64),
	INFO_ID BIGINT,
        CONSTRAINT PK_MODEL_FIELD PRIMARY KEY(ID),
        CONSTRAINT FK_MODEL_FIELD_INFO FOREIGN KEY(INFO_ID) REFERENCES MODEL_INFO(ID)
) ENGINE=INNODB CHARSET=UTF8;

