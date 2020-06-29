

-------------------------------------------------------------------------------
--  doc info
-------------------------------------------------------------------------------
CREATE TABLE DOC_INFO(
        ID BIGINT AUTO_INCREMENT,
	NAME VARCHAR(200),
	PATH VARCHAR(200),
	TYPE INTEGER,
	CREATE_TIME TIMESTAMP,
	USER_ID BIGINT,
        CONSTRAINT PK_DOC_INFO PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

