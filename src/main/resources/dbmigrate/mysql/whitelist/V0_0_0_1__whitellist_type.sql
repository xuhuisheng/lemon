

-------------------------------------------------------------------------------
--  whitelist type
-------------------------------------------------------------------------------
CREATE TABLE WHITELIST_TYPE(
        ID BIGINT NOT NULL,
	NAME VARCHAR(50),
	CODE VARCHAR(50),
        CONSTRAINT PK_WHITELIST_TYPE PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

