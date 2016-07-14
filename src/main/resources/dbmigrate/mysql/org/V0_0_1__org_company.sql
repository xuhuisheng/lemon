

-------------------------------------------------------------------------------
--  org company
-------------------------------------------------------------------------------
CREATE TABLE ORG_COMPANY(
        ID BIGINT NOT NULL,
        CODE VARCHAR(50),
        NAME VARCHAR(200),
	DESCN VARCHAR(200),
        STATUS INTEGER,
	REF VARCHAR(200),
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_ORG_COMPANY PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

