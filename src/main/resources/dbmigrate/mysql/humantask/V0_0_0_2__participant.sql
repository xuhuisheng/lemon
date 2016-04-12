

-------------------------------------------------------------------------------
--  participant
-------------------------------------------------------------------------------
CREATE TABLE HT_PARTICIPANT(
	ID BIGINT NOT NULL,
	CATEGORY VARCHAR(200),
	TYPE VARCHAR(200),
	REF VARCHAR(200),
	HUMANTASK_ID BIGINT,
        CONSTRAINT PK_HT_PARTICIPANT PRIMARY KEY(ID),
	CONSTRAINT FK_HT_PARTICIPANT_HUMANTASK FOREIGN KEY(HUMANTASK_ID) REFERENCES HT_HUMANTASK(ID)
) ENGINE=INNODB CHARSET=UTF8;








