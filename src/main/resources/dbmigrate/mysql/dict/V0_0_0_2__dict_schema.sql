

-------------------------------------------------------------------------------
--  dict schema
-------------------------------------------------------------------------------
CREATE TABLE DICT_SCHEMA(
        ID BIGINT NOT NULL,
        NAME VARCHAR(200),
	TYPE VARCHAR(50),
	PRIORITY INT,
	DESCN VARCHAR(200),
	TYPE_ID BIGINT,
        CONSTRAINT PK_DICT_SCHEMA PRIMARY KEY(ID),
	CONSTRAINT FK_DICT_SCHEMA_TYPE FOREIGN KEY(TYPE_ID) REFERENCES DICT_TYPE(ID)
) ENGINE=INNODB CHARSET=UTF8;










