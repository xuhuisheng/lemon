

-------------------------------------------------------------------------------
--  asset info
-------------------------------------------------------------------------------
CREATE TABLE ASSET_INFO(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	CODE VARCHAR(200),
	DESCRIPTION VARCHAR(200),
	CONSTRAINT PK_ASSET_INFO PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

