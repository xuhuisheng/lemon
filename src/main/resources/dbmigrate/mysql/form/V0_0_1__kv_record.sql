

-------------------------------------------------------------------------------
--  keyvalue record
-------------------------------------------------------------------------------
CREATE TABLE KV_RECORD(
        ID BIGINT AUTO_INCREMENT,
	CATEGORY VARCHAR(200),
	STATUS INT,
	REF VARCHAR(200),
        CONSTRAINT PK_KV_RECORD PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;







