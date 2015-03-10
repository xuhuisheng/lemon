

-------------------------------------------------------------------------------
--  scheduler
-------------------------------------------------------------------------------
CREATE TABLE PIM_SCHEDULER(
        ID BIGINT AUTO_INCREMENT,
	NAME VARCHAR(200),
	LOCATION VARCHAR(200),
	CONTENT VARCHAR(200),
	TYPE INTEGER,
	START_TIME TIMESTAMP,
	END_TIME TIMESTAMP,
	ALERT_TIME VARCHAR(50),
	USER_ID VARCHAR(64),
	STATUS INTEGER,
	REF VARCHAR(100),
        CONSTRAINT PK_PIM_SCHEDULER PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

