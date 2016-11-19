

-------------------------------------------------------------------------------
--  seat info
-------------------------------------------------------------------------------
CREATE TABLE SEAT_INFO(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	TYPE VARCHAR(50),
	BUILDING VARCHAR(100),
	FLOOR VARCHAR(100),
	LOCATION VARCHAR(100),
	DESCRIPTION VARCHAR(200),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_SEAT_INFO PRIMARY KEY(ID)
);

