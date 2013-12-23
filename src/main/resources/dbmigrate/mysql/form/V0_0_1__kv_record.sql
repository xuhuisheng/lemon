

-------------------------------------------------------------------------------
--  keyvalue record
-------------------------------------------------------------------------------
CREATE TABLE KV_RECORD(
        ID BIGINT auto_increment,
	CATEGORY VARCHAR(200),
	STATUS INT,
	REF VARCHAR(200),
        CONSTRAINT PK_KV_RECORD PRIMARY KEY(ID)
) engine=innodb;

