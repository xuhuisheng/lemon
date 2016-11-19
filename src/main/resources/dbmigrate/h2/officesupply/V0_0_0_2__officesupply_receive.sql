

-------------------------------------------------------------------------------
--  officesupply receive
-------------------------------------------------------------------------------
CREATE TABLE OFFICESUPPLY_RECEIVE(
        ID BIGINT NOT NULL,
	INFO_ID BIGINT,
	USER_ID VARCHAR(64),
	RECEIVE_TIME DATETIME,
	RECEIVE_COUNT INT,
	DESCRIPTION VARCHAR(200),
	CONSTRAINT PK_OFFICESUPPLY_RECEIVE PRIMARY KEY(ID),
        CONSTRAINT FK_OFFICESUPPLY_RECEIVE_INFO FOREIGN KEY(INFO_ID) REFERENCES OFFICESUPPLY_INFO(ID)
);


