

-------------------------------------------------------------------------------
--  store batch
-------------------------------------------------------------------------------
CREATE TABLE STORE_BATCH(
    ID BIGINT NOT NULL,
    NAME VARCHAR(50),
    CREATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	STATUS VARCHAR(50),
	DESCRIPTION VARCHAR(200),
    CONSTRAINT PK_STORE_BATCH PRIMARY KEY(ID)
);
