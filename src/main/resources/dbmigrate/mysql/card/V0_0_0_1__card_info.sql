

-------------------------------------------------------------------------------
--  card info
-------------------------------------------------------------------------------
CREATE TABLE CARD_INFO(
        ID BIGINT NOT NULL,
	CODE VARCHAR(200),
	DESCRIPTION VARCHAR(200),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_CARD_INFO PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

