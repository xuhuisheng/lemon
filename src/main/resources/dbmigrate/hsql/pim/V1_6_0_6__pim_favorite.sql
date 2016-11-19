

-------------------------------------------------------------------------------
--  pim favorite
-------------------------------------------------------------------------------
CREATE TABLE PIM_FAVORITE(
        ID BIGINT NOT NULL,
	MODULE_TYPE VARCHAR(50),
	MODULE_ID VARCHAR(64),
	TITLE VARCHAR(100),
        CONTENT VARCHAR(200),
	CREATE_TIME DATETIME,
	TAGS VARCHAR(200),
	USER_ID VARCHAR(64),
        CONSTRAINT PK_PIM_FAVORITE PRIMARY KEY(ID)
);

