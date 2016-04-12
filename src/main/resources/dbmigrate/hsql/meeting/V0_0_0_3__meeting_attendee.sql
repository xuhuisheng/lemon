

-------------------------------------------------------------------------------
--  meeting attendee
-------------------------------------------------------------------------------
CREATE TABLE MEETING_ATTENDEE(
        ID BIGINT NOT NULL,
	USER_ID VARCHAR(64),
	PRIORITY INT,
	TYPE VARCHAR(50),
	TENANT_ID VARCHAR(64),
	INFO_ID BIGINT,
        CONSTRAINT PK_MEETING_ATTENDEE PRIMARY KEY(ID),
        CONSTRAINT FK_MEETING_ATTENDEE_INFO FOREIGN KEY(INFO_ID) REFERENCES MEETING_INFO(ID)
);

