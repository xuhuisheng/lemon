

-------------------------------------------------------------------------------
--  vehicle info
-------------------------------------------------------------------------------
CREATE TABLE VEHICLE_INFO(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	CODE VARCHAR(200),
	STATUS INTEGER,
	WEIGHT INTEGER,
	SEAT INTEGER,
	TYPE VARCHAR(50),
	COLOR VARCHAR(50),
	BUY_DATE DATE,
	CLIVTA_DATE DATE,
	VI_DATE DATE,
	PRICE INTEGER,
	ENGINE_NUMBER VARCHAR(50),
	VIN VARCHAR(50),
	DESCRIPTION VARCHAR(200),
	USING_UNIT VARCHAR(50),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_VEHICLE_INFO PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;



















