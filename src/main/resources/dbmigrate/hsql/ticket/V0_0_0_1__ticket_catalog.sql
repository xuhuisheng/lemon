

-------------------------------------------------------------------------------
--  ticket catalog
-------------------------------------------------------------------------------
CREATE TABLE TICKET_CATALOG(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	DESCRIPTION VARCHAR(200),
        CONSTRAINT PK_TICKET_CATALOG PRIMARY KEY(ID)
);

