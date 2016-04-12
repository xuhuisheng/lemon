

-------------------------------------------------------------------------------
--  pim device
-------------------------------------------------------------------------------
CREATE TABLE PIM_DEVICE(
        ID BIGINT NOT NULL,
	CODE VARCHAR(64),
	NAME VARCHAR(64),
	TYPE VARCHAR(64),
	SESSION_ID VARCHAR(64),
	CREATE_TIME DATETIME,
	STATUS VARCHAR(50),
	USER_ID VARCHAR(64),
        CONSTRAINT PK_PIM_DEVICE PRIMARY KEY(ID)
);

