

-------------------------------------------------------------------------------
--  scheduler participant
-------------------------------------------------------------------------------
CREATE TABLE PIM_SCHEDULER_PARTICIPANT(
        ID BIGINT AUTO_INCREMENT,
	TYPE INTEGER,
	USER_ID VARCHAR(64),
	STATUS INTEGER,
	SCHEDULER_ID BIGINT,
	CONSTRAINT PK_PIM_SCHEDULER_PARTICIPANT PRIMARY KEY(ID),
	CONSTRAINT FK_PIM_SCHEDULER_PARTICIPANT_SCHEDULER FOREIGN KEY(SCHEDULER_ID) REFERENCES PIM_SCHEDULER(ID)
) ENGINE=INNODB CHARSET=UTF8;

