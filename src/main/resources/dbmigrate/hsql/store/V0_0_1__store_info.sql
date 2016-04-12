

-------------------------------------------------------------------------------
--  store info
-------------------------------------------------------------------------------
CREATE TABLE STORE_INFO(
        ID BIGINT NOT NULL,
        NAME VARCHAR(200),
	MODEL VARCHAR(50),
	PATH VARCHAR(200),
	TYPE VARCHAR(50),
	SIZE BIGINT,
	CREATE_TIME TIMESTAMP,
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_STORE_INFO PRIMARY KEY(ID)
);
