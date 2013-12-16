

-------------------------------------------------------------------------------
--  org position
-------------------------------------------------------------------------------
CREATE TABLE ORG_POSITION(
        ID BIGINT auto_increment,
        NAME VARCHAR(200),
	PRIORITY INTEGER,
	TYPE_ID BIGINT,
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_ORG_POSITION PRIMARY KEY(ID),
        CONSTRAINT FK_ORG_POSITION_TYPE FOREIGN KEY(TYPE_ID) REFERENCES ORG_POSITION_TYPE(ID)
) engine=innodb;

