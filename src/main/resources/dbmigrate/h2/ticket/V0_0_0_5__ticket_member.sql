

-------------------------------------------------------------------------------
--  ticket member
-------------------------------------------------------------------------------
CREATE TABLE TICKET_MEMBER(
        ID BIGINT NOT NULL,
	USER BIGINT,
	GROUP_ID BIGINT,
        CONSTRAINT PK_TICKET_MEMBER PRIMARY KEY(ID),
	CONSTRAINT FK_TICKET_MEMBER_GROUP FOREIGN KEY (GROUP_ID) REFERENCES TICKET_GROUP(ID)
);

