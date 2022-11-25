

-------------------------------------------------------------------------------
--  ticket app
-------------------------------------------------------------------------------
CREATE TABLE TICKET_APP(
    ID BIGINT NOT NULL,
    CODE VARCHAR(50),
    NAME VARCHAR(200),
    LOGO VARCHAR(200),
    DESCRIPTION VARCHAR(200),
    CONSTRAINT PK_TICKET_APP PRIMARY KEY(ID)
) ENGINE=INNODB CHARSET=UTF8;

