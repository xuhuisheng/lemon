

-------------------------------------------------------------------------------
--  doc info
-------------------------------------------------------------------------------
CREATE TABLE DOC_INFO(
        ID BIGINT NOT NULL,
	NAME VARCHAR(100),
	CONTENT VARCHAR(100),
	CREATE_TIME DATETIME,
	TENANT_ID VARCHAR(50),
        CONSTRAINT PK_DOC_INFO PRIMARY KEY(ID)
);

