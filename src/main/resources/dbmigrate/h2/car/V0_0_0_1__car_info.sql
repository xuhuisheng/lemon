

-------------------------------------------------------------------------------
--  car info
-------------------------------------------------------------------------------
CREATE TABLE CAR_INFO(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	CODE VARCHAR(200),
	STATUS INTEGER,
	WEIGHT INTEGER,
	PEOPLE INTEGER,
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_CAR_INFO PRIMARY KEY(ID)
);

