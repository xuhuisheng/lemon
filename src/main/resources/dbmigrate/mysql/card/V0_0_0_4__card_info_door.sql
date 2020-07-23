

-------------------------------------------------------------------------------
--  card info door
-------------------------------------------------------------------------------
CREATE TABLE CARD_INFO_DOOR(
	INFO_ID BIGINT,
	DOOR_ID BIGINT,
    CONSTRAINT PK_CARD_INFO_DOOR PRIMARY KEY(INFO_ID, DOOR_ID),
    CONSTRAINT FK_CARD_INFO_DOOR_INFO FOREIGN KEY(INFO_ID) REFERENCES CARD_INFO(ID),
    CONSTRAINT FK_CARD_INFO_DOOR_DOOR FOREIGN KEY(DOOR_ID) REFERENCES DOOR_INFO(ID)
) ENGINE=INNODB CHARSET=UTF8;

