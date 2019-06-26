

-------------------------------------------------------------------------------
--  meeting info
-------------------------------------------------------------------------------
CREATE TABLE MEETING_INFO(
        ID BIGINT NOT NULL,
	SUBJECT VARCHAR(200),
	CONTENT VARCHAR(200),
	CREATE_TIME DATETIME,
	CALENDAR_DATE DATE,
	START_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	END_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	ORGANIZER VARCHAR(64),
	MEETING_TIMEZONE VARCHAR(50),
	TYPE VARCHAR(50),
	CATALOG VARCHAR(50),
	STATUS VARCHAR(50),
	USER_ID VARCHAR(64),
	MEETING_ROOM_ID BIGINT,
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_MEETING_INFO PRIMARY KEY(ID),
        CONSTRAINT FK_MEETING_INFO_ROOM FOREIGN KEY(MEETING_ROOM_ID) REFERENCES MEETING_ROOM(ID)
);

