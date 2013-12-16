

-------------------------------------------------------------------------------
--  org group
-------------------------------------------------------------------------------
CREATE TABLE ORG_GROUP(
        ID BIGINT auto_increment,
        NAME VARCHAR(200),
	DESCN VARCHAR(200),
        STATUS INTEGER,
	REF VARCHAR(200),
	SCOPE_ID VARCHAR(50),
        CONSTRAINT PK_ORG_GROUP PRIMARY KEY(ID)
) engine=innodb;

