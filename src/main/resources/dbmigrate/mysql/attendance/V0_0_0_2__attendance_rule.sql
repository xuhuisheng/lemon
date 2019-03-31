

-------------------------------------------------------------------------------
--  attendance rule
-------------------------------------------------------------------------------
CREATE TABLE ATTENDANCE_RULE(
        ID BIGINT NOT NULL,
	NAME VARCHAR(50),
	START_HOUR INTEGER,
	START_MINUTE INTEGER,
	END_HOUR INTEGER,
	END_MINUTE INTEGER,
	START_OFFSET INTEGER,
	END_OFFSET INTEGER,
	WORKDAY VARCHAR(200),
	TENANT_ID VARCHAR(64),
        CONSTRAINT PK_ATTENDANCE_RULE PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

