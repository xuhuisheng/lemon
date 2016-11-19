

-------------------------------------------------------------------------------
--  org department
-------------------------------------------------------------------------------
CREATE TABLE ORG_DEPARTMENT(
        ID BIGINT NOT NULL,
        CODE VARCHAR(50),
        NAME VARCHAR(200),
	DESCN VARCHAR(200),
        STATUS INTEGER,
	REF VARCHAR(200),
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_ORG_DEPARTMENT PRIMARY KEY(ID)
);

