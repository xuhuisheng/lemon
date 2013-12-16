

-------------------------------------------------------------------------------
--  org position type
-------------------------------------------------------------------------------
CREATE TABLE ORG_POSITION_TYPE(
        ID BIGINT auto_increment,
        NAME VARCHAR(200),
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_ORG_POSITION_TYPE PRIMARY KEY(ID)
) engine=innodb;

