

-------------------------------------------------------------------------------
--  msg info
-------------------------------------------------------------------------------
CREATE TABLE MSG_INFO(
        ID BIGINT AUTO_INCREMENT,
	NAME VARCHAR(200),
	CONTENT TEXT,
	TYPE INTEGER,
	CREATE_TIME TIMESTAMP,
	SENDER_ID VARCHAR(64),
	RECEIVER_ID VARCHAR(64),
	STATUS INTEGER,
        CONSTRAINT PK_MSG_INFO PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

