

-------------------------------------------------------------------------------
--  meeting room
-------------------------------------------------------------------------------
CREATE TABLE MEETING_ROOM(
        ID BIGINT NOT NULL,
	CODE VARCHAR(50),
	NAME VARCHAR(200),
	MAP_REF VARCHAR(200),
	NUM INTEGER,
	DEVICE VARCHAR(200),
	TYPE VARCHAR(50),
	NOTICE VARCHAR(50),
	TIMEZONE VARCHAR(50),
	START_TIME DATETIME,
	END_TIME DATETIME,
	BUILDING VARCHAR(200),
	FLOOR VARCHAR(50),
	STATUS VARCHAR(50),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_MEETING_ROOM PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

