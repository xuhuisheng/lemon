

-------------------------------------------------------------------------------
--  dict info
-------------------------------------------------------------------------------
CREATE TABLE DICT_INFO(
        ID BIGINT auto_increment,
        NAME VARCHAR(200),
	TYPE INTEGER,
	STRING_VALUE VARCHAR(200),
	LONG_VALUE BIGINT,
	DOUBLE_VALUE FLOAT,
        CONSTRAINT PK_DICT_INFO PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

