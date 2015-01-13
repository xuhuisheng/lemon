

-------------------------------------------------------------------------------
--  doc info
-------------------------------------------------------------------------------
CREATE TABLE DOC_INFO(
        ID BIGINT auto_increment,
	NAME VARCHAR(200),
	PATH VARCHAR(200),
	TYPE INTEGER,
	CREATE_TIME TIMESTAMP,
	USER_ID BIGINT,
        CONSTRAINT PK_DOC_INFO PRIMARY KEY(ID)
) engine=innodb;

