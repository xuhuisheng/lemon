

-------------------------------------------------------------------------------
--  cal info
-------------------------------------------------------------------------------
CREATE TABLE CAL_INFO(
        ID BIGINT auto_increment,
	NAME VARCHAR(200),
	ADDRESS VARCHAR(200),
	TYPE INTEGER,
	START_TIME TIMESTAMP,
	END_TIME TIMESTAMP,
	ALERT_TIME VARCHAR(50),
	PRIORITY INTEGER,
	USER_ID BIGINT,
	DESCN VARCHAR(200),
        CONSTRAINT PK_CAL_INFO PRIMARY KEY(ID)
) engine=innodb;

