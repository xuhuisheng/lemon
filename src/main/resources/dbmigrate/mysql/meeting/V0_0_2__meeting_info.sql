

-------------------------------------------------------------------------------
--  meeting info
-------------------------------------------------------------------------------
CREATE TABLE MEETING_INFO(
        ID BIGINT auto_increment,
	NAME VARCHAR(200),
	START_TIME TIMESTAMP,
	END_TIME TIMESTAMP,
	USER_ID BIGINT,
	MEETING_ROOM_ID BIGINT,
        CONSTRAINT PK_MEETING_INFO PRIMARY KEY(ID),
        CONSTRAINT FK_MEETING_INFO_ROOM FOREIGN KEY(MEETING_ROOM_ID) REFERENCES MEETING_ROOM(ID)
) engine=innodb;

