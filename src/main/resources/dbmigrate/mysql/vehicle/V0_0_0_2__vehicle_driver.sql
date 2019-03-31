

-------------------------------------------------------------------------------
--  vehicle driver
-------------------------------------------------------------------------------
CREATE TABLE VEHICLE_DRIVER(
        ID BIGINT NOT NULL,
	NAME VARCHAR(50),
	GENDER VARCHAR(50),
	BIRTHDAY DATE,
	CODE VARCHAR(50),
	LICENSE_DATE DATE,
	EXPIRE_DATE DATE,
	YEAR INTEGER,
	TYPE VARCHAR(50),
	MOBILE VARCHAR(50),
	LOCATION VARCHAR(200),
	ANNUAL_INSPECTION varchar(200),
	STATUS INTEGER,
	DESCRIPTION VARCHAR(200),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_VEHICLE_DRIVER PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

















