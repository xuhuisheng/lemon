

-------------------------------------------------------------------------------
--  meeting room
-------------------------------------------------------------------------------
CREATE TABLE MEETING_ROOM(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	MAP_REF VARCHAR(200),
	NUM INTEGER,
	PROJECTOR VARCHAR(50),
	TYPE VARCHAR(50),
	START_TIME DATETIME,
	END_TIME DATETIME,
	BUILDING VARCHAR(200),
	FLOOR VARCHAR(50),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_MEETING_ROOM PRIMARY KEY(ID)
);

