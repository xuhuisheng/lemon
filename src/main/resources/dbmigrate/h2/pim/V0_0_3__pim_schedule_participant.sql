

-------------------------------------------------------------------------------
--  schedule participant
-------------------------------------------------------------------------------
CREATE TABLE PIM_SCHEDULE_PARTICIPANT(
        ID BIGINT NOT NULL,
	TYPE INTEGER,
	USER_ID VARCHAR(64),
	STATUS INTEGER,
	SCHEDULE_ID BIGINT,
	CONSTRAINT PK_PIM_SCHEDULE_PARTICIPANT PRIMARY KEY(ID),
	CONSTRAINT FK_PIM_SCHEDULE_PARTICIPANT_SCHEDULE FOREIGN KEY(SCHEDULE_ID) REFERENCES PIM_SCHEDULE(ID)
);

