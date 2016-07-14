

-------------------------------------------------------------------------------
--  officesupply info
-------------------------------------------------------------------------------
CREATE TABLE OFFICESUPPLY_INFO(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	CODE VARCHAR(200),
	PRICE DOUBLE,
	UNIT VARCHAR(50),
	TYPE VARCHAR(50),
	PHOTO VARCHAR(200),
	DESCRIPTION VARCHAR(200),
	CONSTRAINT PK_OFFICESUPPLY_INFO PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;


