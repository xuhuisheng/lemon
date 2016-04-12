

-------------------------------------------------------------------------------
--  stamp info
-------------------------------------------------------------------------------
CREATE TABLE STAMP_INFO(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	TYPE VARCHAR(50),
	DESCRIPTION VARCHAR(200),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_STAMP_INFO PRIMARY KEY(ID)
);

