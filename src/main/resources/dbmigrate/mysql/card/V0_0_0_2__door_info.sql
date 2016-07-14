

-------------------------------------------------------------------------------
--  door info
-------------------------------------------------------------------------------
CREATE TABLE DOOR_INFO(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	TYPE VARCHAR(50),
	BUILDING VARCHAR(100),
	FLOOR VARCHAR(100),
	PRIORITY INT,
	DESCRIPTION VARCHAR(200),
        CONSTRAINT PK_DOOR_INFO PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

