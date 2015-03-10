

-------------------------------------------------------------------------------
--  meeting room
-------------------------------------------------------------------------------
CREATE TABLE MEETING_ROOM(
        ID BIGINT AUTO_INCREMENT,
	NAME VARCHAR(200),
	PEOPLE INTEGER,
        CONSTRAINT PK_MEETING_ROOM PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

