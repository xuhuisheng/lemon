

-------------------------------------------------------------------------------
--  plm config
-------------------------------------------------------------------------------
CREATE TABLE PLM_CONFIG(
        ID BIGINT NOT NULL,
	CODE VARCHAR(50),
	NAME VARCHAR(200),
	CONSTRAINT PK_PLM_CONFIG PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

