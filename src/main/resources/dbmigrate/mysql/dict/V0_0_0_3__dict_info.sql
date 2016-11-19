

-------------------------------------------------------------------------------
--  dict info
-------------------------------------------------------------------------------
CREATE TABLE DICT_INFO(
        ID BIGINT NOT NULL,
        NAME VARCHAR(200),
	VALUE VARCHAR(200),
	PRIORITY INT,
	TYPE_ID BIGINT,
        CONSTRAINT PK_DICT_INFO PRIMARY KEY(ID),
	CONSTRAINT FK_DICT_INFO_TYPE FOREIGN KEY(TYPE_ID) REFERENCES DICT_TYPE(ID)
) ENGINE=INNODB CHARSET=UTF8;









