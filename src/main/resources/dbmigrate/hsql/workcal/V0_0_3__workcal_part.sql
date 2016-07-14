

-------------------------------------------------------------------------------
--  workcal part
-------------------------------------------------------------------------------
CREATE TABLE WORKCAL_PART(
        ID BIGINT NOT NULL,
        SHIFT INT,
	START_TIME VARCHAR(5),
	END_TIME VARCHAR(5),
	RULE_ID BIGINT,
        CONSTRAINT PK_WORKCAL_PART PRIMARY KEY(ID),
	CONSTRAINT FK_WORKCAL_PART_RULE FOREIGN KEY(RULE_ID) REFERENCES WORKCAL_RULE(ID)
);

