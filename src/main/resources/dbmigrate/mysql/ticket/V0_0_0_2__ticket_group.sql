

-------------------------------------------------------------------------------
--  ticket group
-------------------------------------------------------------------------------
CREATE TABLE TICKET_GROUP(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	DESCRIPTION VARCHAR(200),
        CONSTRAINT PK_TICKET_GROUP PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

