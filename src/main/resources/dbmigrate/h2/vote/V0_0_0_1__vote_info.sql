

-------------------------------------------------------------------------------
--  vote info
-------------------------------------------------------------------------------
CREATE TABLE VOTE_INFO(
        ID BIGINT NOT NULL,
	NAME VARCHAR(100),
	CONTENT VARCHAR(200),
	USER_ID VARCHAR(64),
	CREATE_TIME DATETIME,
	STATUS VARCHAR(50),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_VOTE_INFO PRIMARY KEY(ID)
);

