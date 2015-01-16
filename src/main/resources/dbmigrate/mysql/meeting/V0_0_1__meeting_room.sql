

-------------------------------------------------------------------------------
--  meeting room
-------------------------------------------------------------------------------
CREATE TABLE MEETING_ROOM(
        ID BIGINT auto_increment,
	NAME VARCHAR(200),
	PEOPLE INTEGER,
        CONSTRAINT PK_MEETING_ROOM PRIMARY KEY(ID)
) engine=innodb;

