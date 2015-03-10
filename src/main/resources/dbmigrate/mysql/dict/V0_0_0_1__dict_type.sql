

-------------------------------------------------------------------------------
--  dict type
-------------------------------------------------------------------------------
CREATE TABLE DICT_TYPE(
        ID BIGINT AUTO_INCREMENT,
        NAME VARCHAR(200),
	TYPE VARCHAR(200),
	DESCN VARCHAR(200),
        CONSTRAINT PK_DICT_TYPE PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;








