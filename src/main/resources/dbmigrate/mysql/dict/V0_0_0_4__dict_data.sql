

-------------------------------------------------------------------------------
--  dict data
-------------------------------------------------------------------------------
CREATE TABLE DICT_DATA(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	VALUE VARCHAR(200),
	INFO_ID BIGINT,
	SCHEMA_ID BIGINT,
        CONSTRAINT PK_DICT_DATA PRIMARY KEY(ID),
	CONSTRAINT FK_DICT_DATA_INFO FOREIGN KEY(INFO_ID) REFERENCES DICT_INFO(ID),
	CONSTRAINT FK_DICT_DATA_SCHEMA FOREIGN KEY(SCHEMA_ID) REFERENCES DICT_SCHEMA(ID)
) ENGINE=INNODB CHARSET=UTF8;









