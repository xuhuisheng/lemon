

-------------------------------------------------------------------------------
--  pim info
-------------------------------------------------------------------------------
CREATE TABLE PIM_INFO(
        ID BIGINT NOT NULL,
        NAME VARCHAR(200),
	PHOTO VARCHAR(200),
	BIRTHDAY DATE,
	ADDRESS VARCHAR(200),
	TEL VARCHAR(200),
	EMAIL VARCHAR(200),
	ORG VARCHAR(200),
	TITLE VARCHAR(200),
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_PIM_INFO PRIMARY KEY(ID)
);
