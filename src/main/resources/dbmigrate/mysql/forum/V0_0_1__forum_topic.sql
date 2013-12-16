

-------------------------------------------------------------------------------
--  forum topic
-------------------------------------------------------------------------------
CREATE TABLE FORUM_TOPIC(
        ID BIGINT auto_increment,
	TITLE VARCHAR(200),
	CONTENT VARCHAR(200),
	CREATE_TIME TIMESTAMP,
	UPDATE_TIME TIMESTAMP,
	HIT_COUNT INTEGER,
	POST_COUNT INTEGER,
	USER_ID BIGINT,
        CONSTRAINT PK_FORUM_TOPIC PRIMARY KEY(ID)
) engine=innodb;

